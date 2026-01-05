package com.app.placify.service;

import com.app.placify.dto.ExportDto;
import com.app.placify.dto.JobListingDto;
import com.app.placify.exceptions.ExternalServiceException;
import com.app.placify.exceptions.InvalidCredentials;
import com.app.placify.exceptions.ResourceNotFoundException;
import com.app.placify.models.JobListing;
import com.app.placify.repository.AppRepo;
import com.app.placify.repository.JobListingRepo;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrcService {

    private final JobListingRepo jobListingRepo;
    private final AppRepo  appRepo;

    public Long createJobListing(JobListingDto jobListing) {
        JobListing jobListingEntity = new JobListing();
        jobListingEntity.setCompanyName(jobListing.getCompanyName());
        jobListingEntity.setJobDescription(jobListing.getJobDescription());
        jobListingEntity.setJobRole(jobListing.getJobRole());
        jobListingEntity.setCtc(jobListing.getCtc());
        jobListingEntity.setDeadline(jobListing.getDeadline());
        jobListingEntity.setJobPostingDateTime(LocalDateTime.now());
        jobListingEntity.setDomain(jobListing.getDomain());
        //Review this i think the crc admin id is left to be added
        JobListing job = jobListingRepo.save(jobListingEntity);
        return job.getJobListingId() ;

    }

    public Page<JobListing> getAllTheListingForAdmin(String search , int page , int size) {
        Pageable pageable =  PageRequest.of(page, size , Sort.by("jobPostingDateTime").descending());
        if(search.isBlank()){
            return jobListingRepo.findAll(pageable);
        }
        return  jobListingRepo.findByCompanyNameContainingIgnoreCase(search,pageable);

    }

    public byte[] exportDataToExcel(Long jobId){
        List<ExportDto> list = appRepo.findApplicantsForJob(jobId);
        String name = getCompanyName(jobId) ;
        if(list.isEmpty()){
            throw new ResourceNotFoundException("No applicants found for this job") ;
        }
        try (Workbook workbook = new XSSFWorkbook()){
            Sheet sheet = workbook.createSheet(name);
            Row top =  sheet.createRow(0);
            top.createCell(0).setCellValue("List of applied students for "+name+" Campus placement drive");
            Row header =  sheet.createRow(1);
            header.createCell(0).setCellValue("RollNo");
            header.createCell(1).setCellValue("Name");
            header.createCell(2).setCellValue("Course");
            header.createCell(3).setCellValue("Branch");
            header.createCell(4).setCellValue("Email");

            int rowNumber = 2 ;
            for(ExportDto dto : list){
                Row row = sheet.createRow(rowNumber++);
                row.createCell(0).setCellValue(dto.getUniversityRollNo());
                row.createCell(1).setCellValue(dto.getName());
                row.createCell(2).setCellValue(dto.getCourse());
                row.createCell(3).setCellValue(dto.getBranch());
                row.createCell(4).setCellValue(dto.getEmail());
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new ExternalServiceException("Export service failed") ;
        }

    }
    public String getCompanyName(Long jobId){
        Optional<JobListing> listing = jobListingRepo.findById(jobId) ;
        if(listing.isEmpty()){
            throw new InvalidCredentials("Invalid job listing id") ;
        }
        String name =  listing.get().getCompanyName();
        return name ;
    }
}

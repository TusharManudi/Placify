package com.app.placify.service;

import com.app.placify.dto.AppliedDto;
import com.app.placify.dto.JobListingResponseDto;
import com.app.placify.models.Application;
import com.app.placify.models.JobListing;
import com.app.placify.models.Student;
import com.app.placify.repository.AppRepo;
import com.app.placify.repository.JobListingRepo;
import com.app.placify.repository.StudentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final JobListingRepo jobListingRepo;
    private final StudentRepo studentRepo;
    private final AppRepo  appRepo;

    public List<JobListingResponseDto> getAllActiveListing(){
        LocalDateTime now = LocalDateTime.now();

        List<JobListing> activeJobs = jobListingRepo.findByDeadlineAfterOrderByDeadlineAsc(now);
        List<JobListingResponseDto> jobListingResponseDtos = new ArrayList<>();

        for(JobListing jobListing : activeJobs){
            jobListingResponseDtos.add(convertListingToDto(jobListing)) ;
        }

        return jobListingResponseDtos;

    }

    public JobListingResponseDto convertListingToDto(JobListing jobListing){
        JobListingResponseDto jobListingResponseDto = new JobListingResponseDto();
        jobListingResponseDto.setId(jobListing.getJobListingId());
        jobListingResponseDto.setCtc(jobListing.getCtc());
        jobListingResponseDto.setDeadline(jobListing.getDeadline());
        jobListingResponseDto.setJobDescription(jobListing.getJobDescription());
        jobListingResponseDto.setCompanyName(jobListing.getCompanyName());
        jobListingResponseDto.setJobRole(jobListing.getJobRole());
        jobListingResponseDto.setDomain(jobListing.getDomain());
        jobListingResponseDto.setJobPostingDate(jobListing.getJobPostingDate());
        return jobListingResponseDto;
    }

    public boolean applyForJob(Long jobId , Long studentId) {
        Optional<Student> student = studentRepo.findById(studentId) ;
        if(student.isEmpty()){
            return false ;
        }
        Optional<JobListing> jobList = jobListingRepo.findById(jobId) ;
        if(jobList.isEmpty()){
            return false ;
        }
        if(LocalDateTime.now().isAfter(jobList.get().getDeadline())){
            return false ;
        }
        Application app = new Application();
        app.setJobListingId(jobList.get().getJobListingId());
        app.setStudentId(student.get().getStudent_id());
        app.setAppliedDate(LocalDate.now());
        app.setAppliedTime(LocalTime.now());
        appRepo.save(app);
        return true ;
    }

    public List<AppliedDto> getAppliedListing(Long studentId){
        List<AppliedDto> appliedDtos = new ArrayList<>();
        List<AppliedDto> list = appRepo.findApplicationByStudentId(studentId) ;
        return list ;
    }
}

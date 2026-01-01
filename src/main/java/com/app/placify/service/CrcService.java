package com.app.placify.service;

import com.app.placify.dto.JobListingDto;
import com.app.placify.models.JobListing;
import com.app.placify.repository.JobListingRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CrcService {

    private final JobListingRepo jobListingRepo;

    public Long createJobListing(JobListingDto jobListing) {
        JobListing jobListingEntity = new JobListing();
        jobListingEntity.setCompanyName(jobListing.getCompanyName());
        jobListingEntity.setJobDescription(jobListing.getJobDescription());
        jobListingEntity.setJobRole(jobListing.getJobRole());
        jobListingEntity.setCtc(jobListing.getCtc());
        jobListingEntity.setDeadline(jobListing.getDeadline());
        jobListingEntity.setJobPostingDateTime(LocalDateTime.now());
        jobListingEntity.setDomain(jobListing.getDomain());
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
}

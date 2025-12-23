package com.app.placify.repository;

import com.app.placify.models.JobListing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface JobListingRepo extends JpaRepository<JobListing, Long> {
    List<JobListing> findByDeadlineAfterOrderByDeadlineAsc(LocalDateTime now);
}

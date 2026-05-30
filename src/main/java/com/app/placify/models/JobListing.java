package com.app.placify.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "job_listing")
public class JobListing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobListingId ;

    private String companyName ;
    private String jobRole ;
    private LocalDateTime jobPostingDateTime ;
    private LocalDateTime deadline ;
    private String ctc ;
    private String location ;
    private String jobDescription ;
    private String domain ;

    //Id of crc official
    //Foreign key
    private Long createdBy ;
}

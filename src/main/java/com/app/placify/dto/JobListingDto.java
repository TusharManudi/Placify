package com.app.placify.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class JobListingDto {
    private String companyName ;
    private String jobRole ;
    private LocalDateTime jobPostingDateTime ;
    private LocalDateTime deadline ;
    private String ctc ;
    private String location ;
    private String jobDescription ;
    private String domain ;
}

package com.app.placify.dto;

import lombok.Builder;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobListingResponseDto {
    private Long id ;
    private String companyName ;
    private String jobRole ;
    private String jobPostingDate ;
    private LocalDateTime deadline ;
    private String ctc ;
    private String location ;
    private String jobDescription ;
    private String domain ;
}

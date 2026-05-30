package com.app.placify.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppliedDto {
        Long applicationId ;
        Long jobListingId;
        LocalDate appliedDate ;
        LocalTime appliedTime ;
        String companyName ;
        String jobRole ;

        public AppliedDto(
                Long applicationId,
                Long jobListingId,
                LocalDate appliedDate ,
                LocalTime appliedTime,
                String companyName,
                String jobRole
        ){
            this.applicationId = applicationId;
            this.jobListingId = jobListingId;
            this.appliedDate = appliedDate;
            this.appliedTime = appliedTime;
            this.companyName = companyName;
            this.jobRole = jobRole;
        }
}

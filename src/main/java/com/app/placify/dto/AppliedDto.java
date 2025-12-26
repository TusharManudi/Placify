package com.app.placify.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppliedDto {
        Long application_id ;
        Long jobListingId;
        LocalDate appliedDate ;
        LocalTime appliedTime ;
        String companyName ;
        String jobRole ;

        public AppliedDto(
                Long application_id,
                Long jobListingId,
                LocalDate appliedDate ,
                LocalTime appliedTime,
                String companyName,
                String jobRole
        ){
            this.application_id = application_id;
            this.jobListingId = jobListingId;
            this.appliedDate = appliedDate;
            this.appliedTime = appliedTime;
            this.companyName = companyName;
            this.jobRole = jobRole;
        }
}

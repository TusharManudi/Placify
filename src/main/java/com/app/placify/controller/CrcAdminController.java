package com.app.placify.controller;

import com.app.placify.dto.JobListingDto;
import com.app.placify.models.JobListing;
import com.app.placify.service.CrcService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import com.app.placify.security.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class CrcAdminController {

    private final CrcService crcService;

    //Endpoint for admins to create a listing
    @PostMapping("/createListing")
    public ResponseEntity<String> createjobListing(@RequestBody JobListingDto jobListingDto, Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long jobId = crcService.createJobListing(jobListingDto, userDetails.getId()) ;
        return ResponseEntity.status(HttpStatus.CREATED).body("Listing created successfully "+jobId.toString());
    }

    //Paginated api to get list of all job listing and search by company name
    @GetMapping("/jobs/getList")
    public ResponseEntity<Page<JobListing>> getPaginatedListing(
            @RequestParam(value= "company" , required = false) String companyName,
            @RequestParam(value= "page" , defaultValue = "0") int page ,
            @RequestParam(value = "size", defaultValue = "20") int size
    ){
            Page<JobListing> model = crcService.getAllTheListingForAdmin(companyName , page , size) ;
            return ResponseEntity.ok().body(model) ;
    }

    //Api that lets download the excel sheet of applicants
    @GetMapping("/{jobId}/export")
    public ResponseEntity<byte[]> exportExcel(@PathVariable("jobId") Long jobId){
        byte[] file = crcService.exportDataToExcel(jobId) ;
        String name = crcService.getCompanyName(jobId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=job_"+name+"_applicants.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }

    @GetMapping("/{jobId}/applicants")
    public ResponseEntity<java.util.List<com.app.placify.dto.ExportDto>> getApplicants(@PathVariable("jobId") Long jobId) {
        java.util.List<com.app.placify.dto.ExportDto> applicants = crcService.getApplicantsForJob(jobId);
        return ResponseEntity.ok(applicants);
    }

    @GetMapping("/profile")
    public ResponseEntity<java.util.Map<String, String>> getAdminProfile(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String name = crcService.getAdminName(userDetails.getId());
        return ResponseEntity.ok(java.util.Map.of("name", name));
    }

}

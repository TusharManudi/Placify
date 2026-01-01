package com.app.placify.controller;

import com.app.placify.dto.JobListingDto;
import com.app.placify.models.JobListing;
import com.app.placify.service.CrcService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@CrossOrigin("*")
public class CrcAdminController {

    private final CrcService crcService;

    @PostMapping("/createListing")
    public ResponseEntity<String> createjobListing(@RequestBody JobListingDto jobListingDto){
        Long jobId = crcService.createJobListing(jobListingDto) ;
        return ResponseEntity.status(HttpStatus.CREATED).body("Listing created successfully"+jobId.toString());
    }

    @GetMapping("/jobs/getList")
    public ResponseEntity<Page<JobListing>> getPaginatedListing(
            @RequestParam(value= "company" , required = false) String companyName,
            @RequestParam(value= "page" , defaultValue = "0") int page ,
            @RequestParam(value = "size", defaultValue = "20") int size
    ){
            Page<JobListing> model = crcService.getAllTheListingForAdmin(companyName , page , size) ;
            return ResponseEntity.ok().body(model) ;
    }

}

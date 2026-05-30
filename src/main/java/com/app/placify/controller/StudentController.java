package com.app.placify.controller;

import com.app.placify.dto.AppliedDto;
import com.app.placify.dto.ApplyDto;
import com.app.placify.dto.JobListingResponseDto;
import com.app.placify.models.Application;
import com.app.placify.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.core.Authentication;
import com.app.placify.security.CustomUserDetails;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@CrossOrigin("*")
public class StudentController {

    private final StudentService studentService;

    // List of currently active jobs that a student can apply to
    @GetMapping("/activejobs")
    public ResponseEntity<List<JobListingResponseDto>> getAllActiveJobListing() {
        List<JobListingResponseDto> responses = studentService.getAllActiveListing();
        return ResponseEntity.ok().body(responses);
    }

    // Apply to a job - will be exposed as a button in the frontend
    @PostMapping("/apply")
    public ResponseEntity<Application> apply(@RequestBody ApplyDto applyDto, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Application applied = studentService.applyForJob(applyDto.getJobId(), userDetails.getId());
        return new ResponseEntity<>(applied, HttpStatus.OK);
    }

    // Get the list of companies that student applied to
    @GetMapping("/applications")
    public ResponseEntity<List<AppliedDto>> getAppliedListing(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<AppliedDto> list = studentService.getAppliedListing(userDetails.getId());
        return ResponseEntity.ok().body(list);
    }

    // Get the student's profile
    @GetMapping("/profile")
    public ResponseEntity<com.app.placify.dto.StudentProfileDto> getProfile(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(studentService.getProfile(userDetails.getId()));
    }

    // Update the student's profile (sensitive fields excluded)
    @PutMapping("/profile")
    public ResponseEntity<com.app.placify.dto.StudentProfileDto> updateProfile(
            @RequestBody com.app.placify.dto.StudentProfileUpdateDto updateDto, 
            Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(studentService.updateProfile(userDetails.getId(), updateDto));
    }

}

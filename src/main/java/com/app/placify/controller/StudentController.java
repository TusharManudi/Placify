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

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    //List of currently active jobs that a student can apply to
    @GetMapping("/activejobs")
    public ResponseEntity<List<JobListingResponseDto>> getAllActiveJobListing(){
        List<JobListingResponseDto> responses = studentService.getAllActiveListing() ;
        return ResponseEntity.ok().body(responses);
    }

    //Apply to a job - will be exposed as a button in the frontend
    @PostMapping("/apply")
    public ResponseEntity<Application> apply(@RequestBody ApplyDto applyDto){
        Application applied = studentService.applyForJob(applyDto.getJobId() , applyDto.getStudentId()) ;
        return new ResponseEntity<>(applied, HttpStatus.OK);
    }

    //Get the list of companies that student applied to
    @GetMapping("/applications")
    public ResponseEntity<List<AppliedDto>> getAppliedListing(@RequestParam Long studentId){
        List<AppliedDto> list = studentService.getAppliedListing(studentId)  ;
        return  ResponseEntity.ok().body(list);
    }
}

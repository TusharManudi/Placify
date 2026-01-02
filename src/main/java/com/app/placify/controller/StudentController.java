package com.app.placify.controller;

import com.app.placify.dto.AppliedDto;
import com.app.placify.dto.ApplyDto;
import com.app.placify.dto.JobListingResponseDto;
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
    public ResponseEntity<String> apply(@RequestBody ApplyDto applyDto){
        boolean isApplied = studentService.applyForJob(applyDto.getJobId() , applyDto.getStudentId()) ;
        if(isApplied){
            return new ResponseEntity<>("Success", HttpStatus.OK);
        }
        return new  ResponseEntity<>("Cannot apply to this job", HttpStatus.BAD_REQUEST);
    }

    //Get the list of companies that student applied to
    @GetMapping("/applications")
    public ResponseEntity<List<AppliedDto>> getAppliedListing(@RequestParam Long studentId){
        List<AppliedDto> list = studentService.getAppliedListing(studentId)  ;
        return  ResponseEntity.ok().body(list);
    }
}

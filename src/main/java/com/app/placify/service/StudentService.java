package com.app.placify.service;

import com.app.placify.dto.AppliedDto;
import com.app.placify.dto.JobListingResponseDto;
import com.app.placify.exceptions.BadRequestException;
import com.app.placify.exceptions.ResourceNotFoundException;
import com.app.placify.models.Application;
import com.app.placify.models.JobListing;
import com.app.placify.models.Student;
import com.app.placify.repository.AppRepo;
import com.app.placify.repository.JobListingRepo;
import com.app.placify.repository.StudentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final JobListingRepo jobListingRepo;
    private final StudentRepo studentRepo;
    private final AppRepo  appRepo;

    public List<JobListingResponseDto> getAllActiveListing(){
        LocalDateTime now = LocalDateTime.now();

        List<JobListing> activeJobs = jobListingRepo.findByDeadlineAfterOrderByDeadlineAsc(now);
        if(activeJobs.isEmpty()){
            throw new ResourceNotFoundException("No active jobs found") ;
        }
        List<JobListingResponseDto> jobListingResponseDtos = new ArrayList<>();

        for(JobListing jobListing : activeJobs){
            jobListingResponseDtos.add(convertListingToDto(jobListing)) ;
        }

        return jobListingResponseDtos;

    }

    public JobListingResponseDto convertListingToDto(JobListing jobListing){
        JobListingResponseDto jobListingResponseDto = new JobListingResponseDto();
        jobListingResponseDto.setId(jobListing.getJobListingId());
        jobListingResponseDto.setCtc(jobListing.getCtc());
        jobListingResponseDto.setDeadline(jobListing.getDeadline());
        jobListingResponseDto.setJobDescription(jobListing.getJobDescription());
        jobListingResponseDto.setCompanyName(jobListing.getCompanyName());
        jobListingResponseDto.setJobRole(jobListing.getJobRole());
        jobListingResponseDto.setDomain(jobListing.getDomain());
        jobListingResponseDto.setJobPostingDate(jobListing.getJobPostingDateTime().toString());
        return jobListingResponseDto;
    }

    public Application applyForJob(Long jobId , Long studentId) {
        Optional<Student> student = studentRepo.findById(studentId) ;
        if(student.isEmpty()){
            throw new ResourceNotFoundException("No student found with id"+studentId);
        }
        Optional<JobListing> jobList = jobListingRepo.findById(jobId) ;
        if(jobList.isEmpty()){
            throw new ResourceNotFoundException("No job found with id"+jobId) ;
        }
        if(LocalDateTime.now().isAfter(jobList.get().getDeadline())){
            throw new BadRequestException("The deadline to apply has passed") ;
        }
        if(appRepo.existsByStudentIdAndJobListingId(studentId, jobId)){
            throw new BadRequestException("You have already applied for this job");
        }
        Application app = new Application();
        app.setJobListingId(jobList.get().getJobListingId());
        app.setStudentId(student.get().getStudentId());
        app.setAppliedDate(LocalDate.now());
        app.setAppliedTime(LocalTime.now());
        return appRepo.save(app);

    }

    public List<AppliedDto> getAppliedListing(Long studentId){
        List<AppliedDto> list = appRepo.fetchApplicationsByStudentId(studentId) ;
        return list ;
    }

    public com.app.placify.dto.StudentProfileDto getProfile(Long studentId) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("No student found with id " + studentId));
        
        com.app.placify.dto.StudentProfileDto dto = new com.app.placify.dto.StudentProfileDto();
        dto.setStudentId(student.getStudentId());
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setPhone(student.getPhone());
        dto.setCourse(student.getCourse());
        dto.setBranch(student.getBranch());
        dto.setResumeUrl(student.getResumeUrl());
        dto.setUniversityRollNo(student.getUniversityRollNo());
        dto.setTenthPercentage(student.getTenthPercentage());
        dto.setTwelfthPercentage(student.getTwelfthPercentage());
        dto.setGraduationCgpa(student.getGraduationCgpa());
        dto.setPostGraduationCgpa(student.getPostGraduationCgpa());
        dto.setCreatedAt(student.getCreatedAt());
        
        return dto;
    }

    public com.app.placify.dto.StudentProfileDto updateProfile(Long studentId, com.app.placify.dto.StudentProfileUpdateDto updateDto) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("No student found with id " + studentId));
        
        student.setName(updateDto.getName());
        student.setPhone(updateDto.getPhone());
        student.setCourse(updateDto.getCourse());
        student.setBranch(updateDto.getBranch());
        student.setResumeUrl(updateDto.getResumeUrl());
        student.setTenthPercentage(updateDto.getTenthPercentage());
        student.setTwelfthPercentage(updateDto.getTwelfthPercentage());
        student.setGraduationCgpa(updateDto.getGraduationCgpa());
        student.setPostGraduationCgpa(updateDto.getPostGraduationCgpa());
        
        studentRepo.save(student);
        return getProfile(studentId);
    }
}

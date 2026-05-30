package com.app.placify.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StudentProfileDto {
    private Long studentId;
    private String name;
    private String email;
    private String phone;
    private String course;
    private String branch;
    private String resumeUrl;
    private String universityRollNo;
    private double tenthPercentage;
    private double twelfthPercentage;
    private double graduationCgpa;
    private double postGraduationCgpa;
    private LocalDateTime createdAt;
}

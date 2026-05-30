package com.app.placify.dto;

import lombok.Data;

@Data
public class StudentProfileUpdateDto {
    private String name;
    private String phone;
    private String course;
    private String branch;
    private String resumeUrl;
    private double tenthPercentage;
    private double twelfthPercentage;
    private double graduationCgpa;
    private double postGraduationCgpa;
}

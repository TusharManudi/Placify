package com.app.placify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ExportDto {
    private String name;
    private String universityRollNo ;
    private String email ;
    private double tenthPercentage ;
    private double twelfthPercentage ;
    private double cgpa ;
    private String course ;
    private String branch ;
}

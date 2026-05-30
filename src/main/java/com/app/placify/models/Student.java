package com.app.placify.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name="students")
public class Student {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long studentId ;

    private String name ;

    private String email ;

    private String password;

    private String role = "ROLE_STUDENT";

    @Column(unique = true , nullable = false )
    private String phone ;

    private String course ;
    private String branch ;
    private String resumeUrl ;

    @Column(unique = true , nullable = false )
    private String universityRollNo ;

    private double tenthPercentage ;
    private double twelfthPercentage ;
    private double graduationCgpa ;
    private double postGraduationCgpa ;

    private LocalDateTime createdAt ;

}

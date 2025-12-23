package com.app.placify.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email ;

    @Column(unique = true , nullable = false )
    private String phone ;

    private LocalDateTime createdAt ;
    //private List<JobListing> listingsMade ;
}

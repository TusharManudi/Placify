package com.app.placify.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long admin_id;

    private String name;
    private String email ;

    private String password;

    private String role = "ROLE_ADMIN";

    @Column(unique = true , nullable = false )
    private String phone ;

    private LocalDateTime createdAt ;
    //private List<JobListing> listingsMade ;
}

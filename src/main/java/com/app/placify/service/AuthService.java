package com.app.placify.service;

import com.app.placify.dto.AdminRegisterRequest;
import com.app.placify.dto.AuthResponse;
import com.app.placify.dto.LoginRequest;
import com.app.placify.dto.StudentRegisterRequest;
import com.app.placify.exceptions.BadRequestException;
import com.app.placify.models.Admin;
import com.app.placify.models.Student;
import com.app.placify.repository.AdminRepo;
import com.app.placify.repository.StudentRepo;
import com.app.placify.security.CustomUserDetails;
import com.app.placify.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final StudentRepo studentRepo;
    private final AdminRepo adminRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse registerStudent(StudentRegisterRequest request) {
        if (studentRepo.findByEmail(request.getEmail()).isPresent() || adminRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        Student student = new Student();
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setPassword(passwordEncoder.encode(request.getPassword()));
        student.setPhone(request.getPhone());
        student.setCourse(request.getCourse());
        student.setBranch(request.getBranch());
        student.setUniversityRollNo(request.getUniversityRollNo());
        student.setCreatedAt(LocalDateTime.now());
        student.setRole("ROLE_STUDENT");

        studentRepo.save(student);

        CustomUserDetails userDetails = new CustomUserDetails(student.getStudentId(), student.getEmail(), student.getPassword(), student.getRole());
        String jwtToken = jwtService.generateToken(userDetails);

        return AuthResponse.builder().token(jwtToken).build();
    }

    public AuthResponse registerAdmin(AdminRegisterRequest request) {
        if (studentRepo.findByEmail(request.getEmail()).isPresent() || adminRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        Admin admin = new Admin();
        admin.setName(request.getName());
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setPhone(request.getPhone());
        admin.setCreatedAt(LocalDateTime.now());
        admin.setRole("ROLE_ADMIN");

        adminRepo.save(admin);

        CustomUserDetails userDetails = new CustomUserDetails(admin.getAdmin_id(), admin.getEmail(), admin.getPassword(), admin.getRole());
        String jwtToken = jwtService.generateToken(userDetails);

        return AuthResponse.builder().token(jwtToken).build();
    }

    public AuthResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // User is authenticated, generate token
        String email = request.getEmail();
        CustomUserDetails userDetails;

        var student = studentRepo.findByEmail(email);
        if (student.isPresent()) {
            Student s = student.get();
            userDetails = new CustomUserDetails(s.getStudentId(), s.getEmail(), s.getPassword(), s.getRole());
        } else {
            var admin = adminRepo.findByEmail(email);
            if (admin.isPresent()) {
                Admin a = admin.get();
                userDetails = new CustomUserDetails(a.getAdmin_id(), a.getEmail(), a.getPassword(), a.getRole());
            } else {
                throw new UsernameNotFoundException("User not found");
            }
        }

        String jwtToken = jwtService.generateToken(userDetails);
        return AuthResponse.builder().token(jwtToken).build();
    }
}

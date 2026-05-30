package com.app.placify.security;

import com.app.placify.models.Admin;
import com.app.placify.models.Student;
import com.app.placify.repository.AdminRepo;
import com.app.placify.repository.StudentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final StudentRepo studentRepo;
    private final AdminRepo adminRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Student> student = studentRepo.findByEmail(email);
        if (student.isPresent()) {
            Student s = student.get();
            return new CustomUserDetails(s.getStudentId(), s.getEmail(), s.getPassword(), s.getRole());
        }

        Optional<Admin> admin = adminRepo.findByEmail(email);
        if (admin.isPresent()) {
            Admin a = admin.get();
            return new CustomUserDetails(a.getAdmin_id(), a.getEmail(), a.getPassword(), a.getRole());
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}

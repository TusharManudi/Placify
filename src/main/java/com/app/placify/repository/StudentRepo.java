package com.app.placify.repository;


import com.app.placify.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StudentRepo extends JpaRepository<Student, Long> {
}

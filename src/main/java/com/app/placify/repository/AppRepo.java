package com.app.placify.repository;

import com.app.placify.models.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRepo extends JpaRepository<Application,Long> {
}

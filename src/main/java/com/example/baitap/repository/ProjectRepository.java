package com.example.baitap.repository;

import com.example.baitap.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
    boolean existsByName(String name);
}


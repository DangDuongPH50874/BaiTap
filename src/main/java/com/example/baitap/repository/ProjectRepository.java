package com.example.baitap.repository;

import com.example.baitap.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
    boolean existsByName(String name);
    List<ProjectEntity> findByMembers_Id(Long userId);
}


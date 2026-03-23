package com.example.baitap.repository;

import com.example.baitap.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    List<TaskEntity> findByAssignedTo_Id(Long userId);

    List<TaskEntity> findByProject_Id(Long projectId);
}


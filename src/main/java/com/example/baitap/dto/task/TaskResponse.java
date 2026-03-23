package com.example.baitap.dto.task;

import com.example.baitap.domain.TaskStatus;

import java.time.LocalDate;

public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDate deadline;
    private Long projectId;
    private Long assignedUserId;

    public TaskResponse(Long id, String title, String description, TaskStatus status, LocalDate deadline,
                         Long projectId, Long assignedUserId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.deadline = deadline;
        this.projectId = projectId;
        this.assignedUserId = assignedUserId;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getAssignedUserId() {
        return assignedUserId;
    }
}


package com.example.baitap.dto.project;

import java.time.LocalDateTime;

public class ProjectMemberResponse {
    private Long projectId;
    private String projectName;
    private Long userId;
    private String username;
    private LocalDateTime addedAt;

    public ProjectMemberResponse() {}

    public ProjectMemberResponse(Long projectId, String projectName, Long userId, String username, LocalDateTime addedAt) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.userId = userId;
        this.username = username;
        this.addedAt = addedAt;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }
}

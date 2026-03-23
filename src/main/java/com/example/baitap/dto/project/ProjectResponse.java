package com.example.baitap.dto.project;

import java.util.List;

public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private Long managerId;
    private List<Long> memberUserIds;

    public ProjectResponse(Long id, String name, String description, Long managerId, List<Long> memberUserIds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.managerId = managerId;
        this.memberUserIds = memberUserIds;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getManagerId() {
        return managerId;
    }

    public List<Long> getMemberUserIds() {
        return memberUserIds;
    }
}


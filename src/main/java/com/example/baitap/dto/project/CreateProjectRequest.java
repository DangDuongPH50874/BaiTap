package com.example.baitap.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CreateProjectRequest {
    @NotBlank(message = "project name is required")
    @Size(min = 3, max = 120, message = "project name length must be 3..120")
    private String name;

    @Size(max = 500, message = "description max length is 500")
    private String description;

    // Members that can have tasks assigned in this project.
    private List<Long> memberUserIds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getMemberUserIds() {
        return memberUserIds;
    }

    public void setMemberUserIds(List<Long> memberUserIds) {
        this.memberUserIds = memberUserIds;
    }
}


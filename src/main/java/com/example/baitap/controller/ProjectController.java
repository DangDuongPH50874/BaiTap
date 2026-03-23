package com.example.baitap.controller;

import com.example.baitap.api.ApiResponse;
import com.example.baitap.dto.project.CreateProjectRequest;
import com.example.baitap.dto.project.ProjectResponse;
import com.example.baitap.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> create(@Valid @RequestBody CreateProjectRequest request) {
        ProjectResponse resp = projectService.createProject(request);
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }
}


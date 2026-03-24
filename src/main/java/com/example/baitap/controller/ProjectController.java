package com.example.baitap.controller;

import com.example.baitap.api.ApiResponse;
import com.example.baitap.dto.project.AddMemberRequest;
import com.example.baitap.dto.project.CreateProjectRequest;
import com.example.baitap.dto.project.ProjectResponse;
import com.example.baitap.dto.project.ProjectMemberResponse;
import com.example.baitap.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getAll() {
        List<ProjectResponse> resp = projectService.getAllProjects();
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getById(@PathVariable Long id) {
        ProjectResponse resp = projectService.getProjectById(id);
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<ApiResponse<ProjectMemberResponse>> addMember(
            @PathVariable Long id,
            @Valid @RequestBody AddMemberRequest request
    ) {
        ProjectMemberResponse resp = projectService.addMember(id, request);
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<ApiResponse<List<ProjectMemberResponse>>> getMembers(@PathVariable Long id) {
        List<ProjectMemberResponse> resp = projectService.getProjectMembers(id);
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }
}


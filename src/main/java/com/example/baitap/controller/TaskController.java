package com.example.baitap.controller;

import com.example.baitap.api.ApiResponse;
import com.example.baitap.dto.task.AssignTaskRequest;
import com.example.baitap.dto.task.CreateTaskRequest;
import com.example.baitap.dto.task.TaskResponse;
import com.example.baitap.dto.task.UpdateTaskStatusRequest;
import com.example.baitap.dto.task.UpdateTaskRequest;
import com.example.baitap.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/tasks")
    public ResponseEntity<ApiResponse<TaskResponse>> create(@Valid @RequestBody CreateTaskRequest request) {
        TaskResponse resp = taskService.create(request);
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }

    @GetMapping("/tasks")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getAll() {
        List<TaskResponse> resp = taskService.getAllTasks();
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }

    @GetMapping("/tasks/project/{projectId}")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> listByProject(@PathVariable("projectId") Long projectId) {
        List<TaskResponse> resp = taskService.listByProject(projectId);
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }

    @GetMapping("/tasks/user/{userId}")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> listByUser(@PathVariable("userId") Long userId) {
        List<TaskResponse> resp = taskService.listByUser(userId);
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }

    @PostMapping("/tasks/{taskId}/assign")
    public ResponseEntity<ApiResponse<TaskResponse>> assign(
            @PathVariable Long taskId,
            @Valid @RequestBody AssignTaskRequest request
    ) {
        TaskResponse resp = taskService.assign(taskId, request);
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }

    @PutMapping("/tasks/{taskId}/status")
    public ResponseEntity<ApiResponse<TaskResponse>> updateStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request
    ) {
        TaskResponse resp = taskService.updateStatus(taskId, request);
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }

    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponse>> update(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskRequest request
    ) {
        TaskResponse resp = taskService.update(taskId, request);
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<ApiResponse<Long>> delete(@PathVariable Long taskId) {
        Long deletedId = taskService.delete(taskId);
        return ResponseEntity.ok(ApiResponse.ok(deletedId));
    }
}


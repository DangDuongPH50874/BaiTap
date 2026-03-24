package com.example.baitap.service;

import com.example.baitap.domain.RoleName;
import com.example.baitap.domain.TaskStatus;
import com.example.baitap.dto.task.AssignTaskRequest;
import com.example.baitap.dto.task.CreateTaskRequest;
import com.example.baitap.dto.task.TaskResponse;
import com.example.baitap.dto.task.UpdateTaskStatusRequest;
import com.example.baitap.dto.task.UpdateTaskRequest;
import com.example.baitap.entity.ProjectEntity;
import com.example.baitap.entity.TaskEntity;
import com.example.baitap.entity.UserEntity;
import com.example.baitap.exception.CustomException;
import com.example.baitap.exception.ErrorCode;
import com.example.baitap.repository.ProjectRepository;
import com.example.baitap.repository.TaskRepository;
import com.example.baitap.repository.UserRepository;
import com.example.baitap.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TaskResponse create(CreateTaskRequest request) {
        if (!SecurityUtils.hasRole(RoleName.MANAGER.name())) {
            throw new CustomException(ErrorCode.FORBIDDEN, "MANAGER required to create task");
        }

        ProjectEntity project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "project not found: " + request.getProjectId()));

        TaskStatus status = request.getStatus() == null ? TaskStatus.TODO : request.getStatus();

        TaskEntity task = new TaskEntity(
                request.getTitle(),
                request.getDescription(),
                status,
                request.getDeadline(),
                project
        );
        TaskEntity saved = taskRepository.save(task);
        return toResponse(saved);
    }

    @Transactional
    public TaskResponse assign(Long taskId, AssignTaskRequest request) {
        if (!SecurityUtils.hasRole(RoleName.MANAGER.name())) {
            throw new CustomException(ErrorCode.FORBIDDEN, "MANAGER required to assign task");
        }

        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "task not found: " + taskId));

        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "user not found: " + request.getUserId()));

        ProjectEntity project = task.getProject();
        boolean userBelongsToProject = project.getMembers().stream()
                .anyMatch(u -> u.getId().equals(user.getId()));

        if (!userBelongsToProject) {
            throw new CustomException(ErrorCode.FORBIDDEN, "user is not a member of this project");
        }

        task.setAssignedTo(user);
        TaskEntity saved = taskRepository.save(task);
        return toResponse(saved);
    }

    @Transactional
    public TaskResponse updateStatus(Long taskId, UpdateTaskStatusRequest request) {
        if (!SecurityUtils.hasRole(RoleName.MANAGER.name())) {
            throw new CustomException(ErrorCode.FORBIDDEN, "MANAGER required to update task status");
        }

        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "task not found: " + taskId));

        if (task.getStatus() == TaskStatus.DONE) {
            throw new CustomException(ErrorCode.BUSINESS_RULE, "cannot update status when task is DONE");
        }

        task.setStatus(request.getStatus());
        TaskEntity saved = taskRepository.save(task);
        return toResponse(saved);
    }

    @Transactional
    public TaskResponse update(Long taskId, UpdateTaskRequest request) {
        if (!SecurityUtils.hasRole(RoleName.MANAGER.name())) {
            throw new CustomException(ErrorCode.FORBIDDEN, "MANAGER required to update task");
        }

        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "task not found: " + taskId));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDeadline(request.getDeadline());

        TaskEntity saved = taskRepository.save(task);
        return toResponse(saved);
    }

    @Transactional
    public Long delete(Long taskId) {
        if (!SecurityUtils.hasRole(RoleName.MANAGER.name())) {
            throw new CustomException(ErrorCode.FORBIDDEN, "MANAGER required to delete task");
        }

        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "task not found: " + taskId));

        taskRepository.delete(task);
        return taskId;
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isUserRole = SecurityUtils.hasRole(RoleName.USER.name());

        if (isUserRole) {
            // USER chỉ xem task của mình
            return taskRepository.findByAssignedTo_Id(currentUserId).stream()
                    .map(this::toResponse)
                    .toList();
        } else {
            // MANAGER xem tất cả tasks
            return taskRepository.findAll().stream()
                    .map(this::toResponse)
                    .toList();
        }
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> listByUser(Long userId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isUserRole = SecurityUtils.hasRole(RoleName.USER.name());

        if (isUserRole && !currentUserId.equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN, "USER can only view own tasks");
        }

        // Both USER (filtered) and MANAGER (all) can use same repository query.
        Long effectiveUserId = isUserRole ? currentUserId : userId;
        return taskRepository.findByAssignedTo_Id(effectiveUserId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> listByProject(Long projectId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isUserRole = SecurityUtils.hasRole(RoleName.USER.name());

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "project not found: " + projectId));

        if (isUserRole) {
            // Rule: USER chỉ xem task của mình.
            boolean belongs = project.getMembers().stream().anyMatch(u -> u.getId().equals(currentUserId));
            if (!belongs) {
                throw new CustomException(ErrorCode.FORBIDDEN, "USER is not a member of this project");
            }
        }

        return taskRepository.findByProject_Id(projectId).stream()
                .filter(t -> !isUserRole || (t.getAssignedTo() != null && t.getAssignedTo().getId().equals(currentUserId)))
                .map(this::toResponse)
                .toList();
    }

    private TaskResponse toResponse(TaskEntity task) {
        Long assignedUserId = task.getAssignedTo() == null ? null : task.getAssignedTo().getId();
        Long projectId = task.getProject().getId();
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDeadline(),
                projectId,
                assignedUserId
        );
    }
}


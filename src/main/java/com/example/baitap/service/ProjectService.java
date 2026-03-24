package com.example.baitap.service;

import com.example.baitap.api.ApiResponse;
import com.example.baitap.domain.RoleName;
import com.example.baitap.dto.project.AddMemberRequest;
import com.example.baitap.dto.project.CreateProjectRequest;
import com.example.baitap.dto.project.ProjectMemberResponse;
import com.example.baitap.dto.project.ProjectResponse;
import com.example.baitap.entity.ProjectEntity;
import com.example.baitap.entity.RoleEntity;
import com.example.baitap.entity.UserEntity;
import com.example.baitap.exception.CustomException;
import com.example.baitap.exception.ErrorCode;
import com.example.baitap.repository.ProjectRepository;
import com.example.baitap.repository.UserRepository;
import com.example.baitap.security.SecurityUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public ProjectResponse createProject(CreateProjectRequest request) {
        if (!SecurityUtils.hasRole(RoleName.MANAGER.name())) {
            throw new CustomException(ErrorCode.FORBIDDEN, "MANAGER required to create project");
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();
        UserEntity manager = userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED, "invalid token user"));

        if (projectRepository.existsByName(request.getName())) {
            throw new CustomException(ErrorCode.CONFLICT, "project name already exists");
        }

        ProjectEntity project = new ProjectEntity(request.getName(), request.getDescription(), manager);
        project.addMember(manager);

        if (request.getMemberUserIds() != null) {
            for (Long memberId : request.getMemberUserIds()) {
                if (memberId == null) {
                    continue;
                }
                UserEntity member = userRepository.findById(memberId)
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "user not found: " + memberId));
                project.addMember(member);
            }
        }

        ProjectEntity saved = projectRepository.save(project);

        List<Long> memberIds = saved.getMembers().stream().map(UserEntity::getId).toList();
        return new ProjectResponse(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getManager().getId(),
                memberIds
        );
    }

    public List<ProjectResponse> getAllProjects() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isUserRole = SecurityUtils.hasRole(RoleName.USER.name());

        if (isUserRole) {
            // USER chỉ xem project của mình
            return projectRepository.findByMembers_Id(currentUserId).stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        } else {
            // MANAGER xem tất cả projects
            return projectRepository.findAll().stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }
    }

    public ProjectResponse getProjectById(Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isUserRole = SecurityUtils.hasRole(RoleName.USER.name());

        ProjectEntity project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "project not found: " + id));

        if (isUserRole) {
            // USER chỉ xem project của mình
            boolean isMember = project.getMembers().stream().anyMatch(u -> u.getId().equals(currentUserId));
            if (!isMember) {
                throw new CustomException(ErrorCode.FORBIDDEN, "USER is not a member of this project");
            }
        }

        return toResponse(project);
    }

    public ProjectMemberResponse addMember(Long projectId, AddMemberRequest request) {
        SecurityUtils.requireRole(RoleName.MANAGER);

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "project not found: " + projectId));

        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "user not found: " + request.getUserId()));

        // Check if user is already a member
        if (project.getMembers().stream().anyMatch(u -> u.getId().equals(user.getId()))) {
            throw new CustomException(ErrorCode.CONFLICT, "user is already a member of this project");
        }

        project.getMembers().add(user);
        projectRepository.save(project);

        return new ProjectMemberResponse(
                project.getId(),
                project.getName(),
                user.getId(),
                user.getUsername(),
                LocalDateTime.now()
        );
    }

    public List<ProjectMemberResponse> getProjectMembers(Long projectId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isUserRole = SecurityUtils.hasRole(RoleName.USER.name());

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "project not found: " + projectId));

        if (isUserRole) {
            // USER chỉ xem project của mình
            boolean isMember = project.getMembers().stream().anyMatch(u -> u.getId().equals(currentUserId));
            if (!isMember) {
                throw new CustomException(ErrorCode.FORBIDDEN, "USER is not a member of this project");
            }
        }

        return project.getMembers().stream()
                .map(member -> new ProjectMemberResponse(
                        project.getId(),
                        project.getName(),
                        member.getId(),
                        member.getUsername(),
                        LocalDateTime.now() // or actual join date if tracked
                ))
                .collect(Collectors.toList());
    }

    private ProjectResponse toResponse(ProjectEntity project) {
        List<Long> memberIds = project.getMembers().stream().map(UserEntity::getId).toList();
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getManager().getId(),
                memberIds
        );
    }
}


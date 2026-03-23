package com.example.baitap.service;

import com.example.baitap.api.ApiResponse;
import com.example.baitap.domain.RoleName;
import com.example.baitap.dto.project.CreateProjectRequest;
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
}


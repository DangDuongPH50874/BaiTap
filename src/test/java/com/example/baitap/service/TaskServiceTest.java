package com.example.baitap.service;

import com.example.baitap.domain.RoleName;
import com.example.baitap.domain.TaskStatus;
import com.example.baitap.dto.task.AssignTaskRequest;
import com.example.baitap.dto.task.CreateTaskRequest;
import com.example.baitap.dto.task.TaskResponse;
import com.example.baitap.dto.task.UpdateTaskRequest;
import com.example.baitap.dto.task.UpdateTaskStatusRequest;
import com.example.baitap.entity.ProjectEntity;
import com.example.baitap.entity.TaskEntity;
import com.example.baitap.entity.UserEntity;
import com.example.baitap.exception.CustomException;
import com.example.baitap.exception.ErrorCode;
import com.example.baitap.repository.ProjectRepository;
import com.example.baitap.repository.TaskRepository;
import com.example.baitap.repository.UserRepository;
import com.example.baitap.security.SecurityUtils;
import com.example.baitap.security.UserPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void create_shouldCreateTaskWithDefaultTodoStatus() throws Exception {
        setAuth(1L, Set.of(RoleName.MANAGER.name()));

        LocalDate deadline = LocalDate.now().plusDays(5);
        CreateTaskRequest req = new CreateTaskRequest();
        setField(req, "projectId", 10L);
        setField(req, "title", "T1");
        setField(req, "description", "D1");
        setField(req, "deadline", deadline);
        setField(req, "status", null);

        UserEntity manager = new UserEntity("manager", "hash");
        setField(manager, "id", 1L);
        ProjectEntity project = new ProjectEntity("P1", "desc", manager);
        setField(project, "id", 10L);

        when(projectRepository.findById(10L)).thenReturn(java.util.Optional.of(project));
        when(taskRepository.save(any(TaskEntity.class))).thenAnswer(inv -> {
            TaskEntity t = inv.getArgument(0);
            setField(t, "id", 99L);
            return t;
        });

        TaskResponse resp = taskService.create(req);

        assertNotNull(resp.getId());
        assertEquals(10L, resp.getProjectId());
        assertEquals(TaskStatus.TODO, resp.getStatus());
        assertEquals("T1", resp.getTitle());

        verify(projectRepository).findById(10L);
        verify(taskRepository).save(any(TaskEntity.class));
    }

    @Test
    void assign_shouldFailIfUserNotInProject() throws Exception {
        setAuth(1L, Set.of(RoleName.MANAGER.name()));

        UserEntity manager = new UserEntity("manager", "hash");
        setField(manager, "id", 1L);

        UserEntity member = new UserEntity("member", "hash");
        setField(member, "id", 2L);

        ProjectEntity project = new ProjectEntity("P1", "desc", manager);
        project.addMember(member); // member belongs to project
        setField(project, "id", 10L);

        TaskEntity task = new TaskEntity("T1", "D1", TaskStatus.TODO, LocalDate.now().plusDays(5), project);
        setField(task, "id", 100L);

        UserEntity outsider = new UserEntity("outsider", "hash");
        setField(outsider, "id", 3L);

        when(taskRepository.findById(100L)).thenReturn(java.util.Optional.of(task));
        when(userRepository.findById(3L)).thenReturn(java.util.Optional.of(outsider));

        AssignTaskRequest req = new AssignTaskRequest();
        setField(req, "userId", 3L);

        CustomException ex = assertThrows(CustomException.class, () -> taskService.assign(100L, req));
        assertEquals(ErrorCode.FORBIDDEN, ex.getErrorCode());

        verify(taskRepository).findById(100L);
        verify(userRepository).findById(3L);
        verify(taskRepository, never()).save(any(TaskEntity.class));
    }

    @Test
    void updateStatus_shouldFailWhenTaskDone() throws Exception {
        setAuth(1L, Set.of(RoleName.MANAGER.name()));

        UserEntity manager = new UserEntity("manager", "hash");
        setField(manager, "id", 1L);
        ProjectEntity project = new ProjectEntity("P1", "desc", manager);
        setField(project, "id", 10L);

        TaskEntity task = new TaskEntity("T1", "D1", TaskStatus.DONE, LocalDate.now().plusDays(5), project);
        setField(task, "id", 100L);

        when(taskRepository.findById(100L)).thenReturn(java.util.Optional.of(task));

        UpdateTaskStatusRequest req = new UpdateTaskStatusRequest();
        setField(req, "status", TaskStatus.IN_PROGRESS);

        CustomException ex = assertThrows(CustomException.class, () -> taskService.updateStatus(100L, req));
        assertEquals(ErrorCode.BUSINESS_RULE, ex.getErrorCode());

        verify(taskRepository).findById(100L);
        verify(taskRepository, never()).save(any(TaskEntity.class));
    }

    @Test
    void update_shouldUpdateTitleDescriptionAndDeadline() throws Exception {
        setAuth(1L, Set.of(RoleName.MANAGER.name()));

        UserEntity manager = new UserEntity("manager", "hash");
        setField(manager, "id", 1L);
        ProjectEntity project = new ProjectEntity("P1", "desc", manager);
        setField(project, "id", 10L);

        TaskEntity task = new TaskEntity("T1", "D1", TaskStatus.TODO, LocalDate.now().plusDays(5), project);
        setField(task, "id", 100L);
        when(taskRepository.findById(100L)).thenReturn(java.util.Optional.of(task));
        when(taskRepository.save(any(TaskEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateTaskRequest req = new UpdateTaskRequest();
        setField(req, "title", "T2");
        setField(req, "description", "D2");
        setField(req, "deadline", LocalDate.now().plusDays(20));

        TaskResponse resp = taskService.update(100L, req);

        assertEquals("T2", resp.getTitle());
        assertEquals("D2", resp.getDescription());
        verify(taskRepository).findById(100L);
        verify(taskRepository).save(any(TaskEntity.class));
    }

    @Test
    void delete_shouldDeleteTask() throws Exception {
        setAuth(1L, Set.of(RoleName.MANAGER.name()));

        UserEntity manager = new UserEntity("manager", "hash");
        setField(manager, "id", 1L);
        ProjectEntity project = new ProjectEntity("P1", "desc", manager);
        setField(project, "id", 10L);

        TaskEntity task = new TaskEntity("T1", "D1", TaskStatus.TODO, LocalDate.now().plusDays(5), project);
        setField(task, "id", 100L);
        when(taskRepository.findById(100L)).thenReturn(java.util.Optional.of(task));

        Long deleted = taskService.delete(100L);
        assertEquals(100L, deleted);

        verify(taskRepository).findById(100L);
        verify(taskRepository).delete(task);
    }

    private void setAuth(Long userId, Set<String> roleNames) {
        var authorities = roleNames.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .toList();
        UserPrincipal principal = new UserPrincipal(userId, "u" + userId, authorities);
        var auth = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }
}


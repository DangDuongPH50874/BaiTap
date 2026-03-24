package com.example.baitap.controller;

import com.example.baitap.api.ApiResponse;
import com.example.baitap.dto.auth.LoginRequest;
import com.example.baitap.dto.auth.LoginResponse;
import com.example.baitap.dto.auth.RegisterRequest;
import com.example.baitap.dto.user.UserResponse;
import com.example.baitap.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<ApiResponse<Object>> register(@Valid @RequestBody RegisterRequest request) {
        var user = userService.register(request);
        return ResponseEntity.ok(ApiResponse.ok(user.getId()));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse resp = userService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }

    @GetMapping("/users/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        UserResponse resp = userService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> resp = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }
}


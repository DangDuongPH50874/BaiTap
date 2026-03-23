package com.example.baitap.controller;

import com.example.baitap.api.ApiResponse;
import com.example.baitap.dto.auth.LoginRequest;
import com.example.baitap.dto.auth.LoginResponse;
import com.example.baitap.dto.auth.RegisterRequest;
import com.example.baitap.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> register(@Valid @RequestBody RegisterRequest request) {
        var user = userService.register(request);
        return ResponseEntity.ok(ApiResponse.ok(user.getId()));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse resp = userService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }
}


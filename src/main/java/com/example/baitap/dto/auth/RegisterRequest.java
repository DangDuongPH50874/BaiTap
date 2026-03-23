package com.example.baitap.dto.auth;

import com.example.baitap.domain.RoleName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank(message = "username is required")
    @Size(min = 3, max = 64, message = "username length must be 3..64")
    private String username;

    @NotBlank(message = "password is required")
    @Size(min = 6, max = 128, message = "password length must be 6..128")
    private String password;

    // Simple: allow choosing role for demo (MANAGER needed by checklist).
    private RoleName role = RoleName.USER;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RoleName getRole() {
        return role;
    }

    public void setRole(RoleName role) {
        this.role = role;
    }
}


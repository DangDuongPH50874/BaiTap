package com.example.baitap.service;

import com.example.baitap.domain.RoleName;
import com.example.baitap.dto.auth.LoginResponse;
import com.example.baitap.dto.auth.RegisterRequest;
import com.example.baitap.entity.RoleEntity;
import com.example.baitap.entity.UserEntity;
import com.example.baitap.exception.CustomException;
import com.example.baitap.exception.ErrorCode;
import com.example.baitap.repository.RoleRepository;
import com.example.baitap.repository.UserRepository;
import com.example.baitap.security.JwtUtil;
import com.example.baitap.security.UserPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                        PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public UserEntity register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(ErrorCode.CONFLICT, "username already exists");
        }

        RoleName roleName = request.getRole() == null ? RoleName.USER : request.getRole();
        RoleEntity role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "role not found: " + roleName));

        String hash = passwordEncoder.encode(request.getPassword());
        UserEntity user = new UserEntity(request.getUsername(), hash);
        user.getRoles().add(role);
        return userRepository.save(user);
    }

    @Transactional
    public LoginResponse login(String username, String password) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED, "invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "invalid credentials");
        }

        List<String> roleNames = user.getRoles().stream().map(r -> r.getName().name()).toList();
        var authorities = roleNames.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)).toList();
        UserPrincipal principal = new UserPrincipal(user.getId(), user.getUsername(), authorities);
        String token = jwtUtil.generateToken(principal, roleNames);
        return new LoginResponse(user.getId(), token);
    }
}


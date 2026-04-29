package com.devx.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devx.auth.domain.User;
import com.devx.auth.dto.AuthResponse;
import com.devx.auth.dto.DashboardResponse;
import com.devx.auth.dto.LoginRequest;
import com.devx.auth.security.JwtService;
import com.devx.auth.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> dashboard() {
        try {
            DashboardResponse response = userService.getDashboard();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {

        User user = userService.findByEmail(request.email());

        if (!Boolean.TRUE.equals(user.getActive())) {
            return ResponseEntity.status(403).build();
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            return ResponseEntity.status(401).build();
        }

        // 🔥 Atualiza último login (novo fluxo)
        userService.updateLastLogin(user.getEmail());

        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(new AuthResponse(token, "Bearer"));
    }
}
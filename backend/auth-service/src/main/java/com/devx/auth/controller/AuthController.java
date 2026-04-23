package com.devx.auth.controller;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.devx.auth.security.JwtService;
import com.devx.auth.domain.User;
import com.devx.auth.dto.LoginRequest;
import com.devx.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.devx.auth.dto.AuthResponse;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {

        User user = userService.findByEmail(request.email());

        if (user == null) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid credentials"
            );
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid credentials"
            );
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(token, "Bearer");
    }
    
}
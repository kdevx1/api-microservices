package com.devx.auth.dto;

import java.time.LocalDateTime;

public record UserResponse(
    Long id,
    String name,
    String email,
    String role,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime lastLoginAt
) {}
package com.devx.auth.dto;

import java.time.LocalDateTime;

public record CategoryResponse(
    Long id,
    String name,
    String description,
    String color,
    Boolean active,
    long serviceCount,
    LocalDateTime createdAt
) {}
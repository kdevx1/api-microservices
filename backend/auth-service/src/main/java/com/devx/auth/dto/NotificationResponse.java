package com.devx.auth.dto;

import java.time.LocalDateTime;

public record NotificationResponse(
    Long id,
    String title,
    String description,
    String type,
    Boolean read,
    LocalDateTime createdAt
) {}
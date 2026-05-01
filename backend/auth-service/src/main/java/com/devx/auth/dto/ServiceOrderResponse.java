package com.devx.auth.dto;

import java.time.LocalDateTime;

public record ServiceOrderResponse(
    Long id,
    String serviceName,
    String serviceCategory,
    String clientName,
    String clientEmail,
    String status,
    LocalDateTime scheduledAt,
    String notes,
    LocalDateTime createdAt
) {}
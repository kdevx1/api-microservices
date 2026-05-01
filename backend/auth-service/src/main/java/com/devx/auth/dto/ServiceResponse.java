package com.devx.auth.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ServiceResponse(
    Long id,
    String name,
    String description,
    Long categoryId,
    String categoryName,
    String categoryColor,
    BigDecimal price,
    Integer durationMinutes,
    String type,
    Boolean active,
    LocalDateTime createdAt
) {}
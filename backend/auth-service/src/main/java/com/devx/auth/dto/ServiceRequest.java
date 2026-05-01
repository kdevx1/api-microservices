package com.devx.auth.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ServiceRequest(

    @NotBlank(message = "Nome obrigatório")
    String name,

    String description,

    @NotNull(message = "Categoria obrigatória")
    Long categoryId,

    @NotNull(message = "Preço obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Preço deve ser maior que zero")
    BigDecimal price,

    Integer durationMinutes,

    @NotBlank(message = "Tipo obrigatório")
    String type
) {}
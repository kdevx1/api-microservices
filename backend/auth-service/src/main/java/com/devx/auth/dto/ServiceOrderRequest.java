package com.devx.auth.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record ServiceOrderRequest(

    @NotNull(message = "Serviço obrigatório")
    Long serviceId,

    @NotNull(message = "Cliente obrigatório")
    Long clientId,

    LocalDateTime scheduledAt,

    String notes
) {}
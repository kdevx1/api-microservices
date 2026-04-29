package com.devx.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

    @NotBlank(message = "Email obrigatório")
    @Email(message = "Email inválido")
    String email,

    @NotBlank(message = "Senha obrigatória")
    String password
) {}
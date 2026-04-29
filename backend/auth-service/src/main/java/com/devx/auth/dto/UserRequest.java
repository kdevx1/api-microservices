package com.devx.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(

    @NotBlank(message = "Nome obrigatório")
    String name,

    @NotBlank(message = "Email obrigatório")
    @Email(message = "Email inválido")
    String email,

    @NotBlank(message = "Senha obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    String password,

    String role
) {}
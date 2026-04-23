package com.devx.auth.dto;

import jakarta.validation.constraints.Email;

public record UserRequest(
    Long id,

    String name,

    @Email(message = "Email inválido")
    String email,

    String password,

    String role

) {}
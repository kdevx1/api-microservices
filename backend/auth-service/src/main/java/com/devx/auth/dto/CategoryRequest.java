package com.devx.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CategoryRequest(

    @NotBlank(message = "Nome obrigatório")
    String name,

    String description,

    @NotBlank(message = "Cor obrigatória")
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Cor deve ser um hex válido ex: #534AB7")
    String color
) {}
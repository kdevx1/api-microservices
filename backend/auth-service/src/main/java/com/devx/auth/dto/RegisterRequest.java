package com.devx.auth.dto;

public record RegisterRequest(
    String email,
    String password
) {}
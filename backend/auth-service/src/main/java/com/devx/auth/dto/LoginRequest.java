package com.devx.auth.dto;

public record LoginRequest(
    String email,
    String password
) {}
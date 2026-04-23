package com.devx.auth.dto;

public record AuthResponse(
    String accessToken,
    String tokenType
) {}
package com.devx.auth.dto;

public record UpdateUserRequest(
    String name,
    String password,
    String role,
    Boolean active
) {}
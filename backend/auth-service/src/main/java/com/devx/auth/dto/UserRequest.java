package com.devx.auth.dto;

public record UserRequest(
    Long id,
    String name,
    String password,
    String email,
    String role
) {}
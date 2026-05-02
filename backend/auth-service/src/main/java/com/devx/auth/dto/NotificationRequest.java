package com.devx.auth.dto;

public record NotificationRequest(
    String title,
    String description,
    String type
) {}
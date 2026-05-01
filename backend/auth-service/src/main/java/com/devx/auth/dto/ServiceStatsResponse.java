package com.devx.auth.dto;

public record ServiceStatsResponse(
    long totalServices,
    long activeServices,
    long totalCategories,
    long totalOrders,
    long pendingOrders,
    double totalRevenue
) {}
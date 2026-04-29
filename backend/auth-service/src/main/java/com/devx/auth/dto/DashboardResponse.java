package com.devx.auth.dto;

public record DashboardResponse(
    long totalUsers,
    long activeUsers,
    long admins
) { @Override
    public String toString() {
        return "Dashboard{total=" + totalUsers + ", active=" + activeUsers + ", admins=" + admins + "}";
    }}
package com.devx.auth.enums;

import java.util.List;

public enum Role {
    ROLE_ADMIN(List.of("ADMIN_PANEL", "DASHBOARD_VIEW")),
    ROLE_USER(List.of("DASHBOARD_VIEW"));

    private final List<String> permissions;

    Role(List<String> permissions) {
        this.permissions = permissions;
    }

    public List<String> getPermissions() {
        return permissions;
    }
}
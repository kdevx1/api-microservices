package com.devx.auth.enums;

import java.util.List;

public enum Role {

    ROLE_USER(List.of("READ")),
    ROLE_ADMIN(List.of("READ", "WRITE", "DELETE", "ALTER"));

    private final List<String> permissions;

    Role(List<String> permissions) {
        System.out.println("ROLE PERM NAME: " + this.name());
        this.permissions = permissions;
    }

    public List<String> getPermissions() {
        System.out.println("GETPERM NAME: " + this.name());
        return permissions;
    }
}

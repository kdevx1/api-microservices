package com.devx.auth.especification;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.devx.auth.domain.User;
import com.devx.auth.enums.Role;

public class UserSpecification {

    // =========================
    // EMAIL
    // =========================
    public static Specification<User> emailEquals(String email) {
        return (root, query, cb) -> {
            if (email == null || email.isBlank()) return null;

            return cb.equal(
                    cb.lower(root.get("email")),
                    email.trim().toLowerCase()
            );
        };
    }

    // =========================
    // NAME
    // =========================
    public static Specification<User> nameContains(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) return null;

            return cb.like(
                    cb.lower(root.get("name")),
                    "%" + name.trim().toLowerCase() + "%"
            );
        };
    }

    // =========================
    // ROLE (SAFE)
    // =========================
    public static Specification<User> roleEquals(String role) {
        return (root, query, cb) -> {
            if (role == null || role.isBlank()) return null;

            try {
                Role parsedRole = Role.valueOf(role.trim().toUpperCase());
                return cb.equal(root.get("role"), parsedRole);
            } catch (Exception e) {
                return null; // evita crash
            }
        };
    }

    // =========================
    // ACTIVE
    // =========================
    public static Specification<User> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) return null;

            return cb.equal(root.get("active"), active);
        };
    }

    // =========================
    // CREATED BETWEEN
    // =========================
    public static Specification<User> createdBetween(
            LocalDateTime from,
            LocalDateTime to
    ) {
        return (root, query, cb) -> {

            if (from == null && to == null) return null;

            if (from != null && to != null) {
                return cb.between(root.get("createdAt"), from, to);
            }

            if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("createdAt"), from);
            }

            return cb.lessThanOrEqualTo(root.get("createdAt"), to);
        };
    }

    // =========================
    // LAST LOGIN BETWEEN (EXTRA)
    // =========================
    public static Specification<User> lastLoginBetween(
            LocalDateTime from,
            LocalDateTime to
    ) {
        return (root, query, cb) -> {

            if (from == null && to == null) return null;

            if (from != null && to != null) {
                return cb.between(root.get("lastLoginAt"), from, to);
            }

            if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("lastLoginAt"), from);
            }

            return cb.lessThanOrEqualTo(root.get("lastLoginAt"), to);
        };
    }
}
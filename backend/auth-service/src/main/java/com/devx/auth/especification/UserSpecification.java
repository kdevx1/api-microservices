package com.devx.auth.especification;

import com.devx.auth.domain.User;
import com.devx.auth.enums.Role;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> emailEquals(String email) {
        return (root, query, cb) ->
                email == null ? null : cb.equal(root.get("email"), email);
    }

    public static Specification<User> nameContains(String name) {
        return (root, query, cb) ->
                name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<User> roleEquals(String role) {
        return (root, query, cb) ->
                role == null ? null : cb.equal(root.get("role"), Role.valueOf(role));
    }
}
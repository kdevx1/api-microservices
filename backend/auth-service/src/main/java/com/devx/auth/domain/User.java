package com.devx.auth.domain;

import  com.devx.auth.enums.Role;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    public List<String> getAuthorities() {
        List<String> authorities = new ArrayList<>();

        authorities.add(this.role.name());
        authorities.addAll(this.role.getPermissions());

        return authorities;
    }
    
    @Column(name = "avatar")
    private String avatar;
}
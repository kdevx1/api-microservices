package com.devx.auth.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.devx.auth.domain.User;
import com.devx.auth.enums.Role;

public interface UserRepository extends 
        JpaRepository<User, Long>, 
        JpaSpecificationExecutor<User> {

    // =============================
    // AUTH
    // =============================

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByEmailIgnoreCaseAndActiveTrue(String email);

    boolean existsByEmailIgnoreCase(String email);

    // =============================
    // FILTERS (ESCALÁVEL)
    // =============================

    Page<User> findByRole(Role role, Pageable pageable);

    Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<User> findByActiveTrue(Pageable pageable);

    // =============================
    // COMBINAÇÕES ÚTEIS
    // =============================

    Page<User> findByNameContainingIgnoreCaseAndActiveTrue(String name, Pageable pageable);

    Page<User> findByRoleAndActiveTrue(Role role, Pageable pageable);

    long countByActiveTrue();

    long countByRole(Role role);
}
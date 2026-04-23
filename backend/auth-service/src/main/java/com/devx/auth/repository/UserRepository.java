package com.devx.auth.repository;

import com.devx.auth.domain.User;
import com.devx.auth.enums.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends 
        JpaRepository<User, Long>, 
        JpaSpecificationExecutor<User> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<User> findByRole(Role role);

    List<User> findByNameContainingIgnoreCase(String name);

    
}
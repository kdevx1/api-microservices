package com.devx.auth.service;

import com.devx.auth.domain.User;
import com.devx.auth.enums.Role;
import com.devx.auth.especification.UserSpecification;
import com.devx.auth.repository.UserRepository;
import com.devx.auth.dto.PageResponse;
import com.devx.auth.dto.UpdateUserRequest;
import com.devx.auth.dto.UserRequest;
import com.devx.auth.dto.UserResponse;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public List<UserResponse> findAll() {
        return userRepository.findAll()
            .stream()
            .map(user -> new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
            ))  
            .toList();
    }

    public UserResponse create(UserRequest request) {

        if (userRepository.existsByEmailIgnoreCase(request.email())){
            throw new RuntimeException("Email already exists");
        }

        User user = new User();

        user.setName(request.name()); 
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        Role role;
        try {
            role = request.role() != null
                    ? Role.valueOf(request.role())
                    : Role.ROLE_ADMIN;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + request.role());
        }

        user.setRole(role);
        System.out.println("ROLE: " + user.getRole());
        System.out.println("AUTHORITIES: " + user.getAuthorities());
        userRepository.save(user);

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    public UserResponse update(Long id, UpdateUserRequest request) {

        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.name() != null) {
            user.setName(request.name());
        }

        if (request.role() != null) {
            user.setRole(Role.valueOf(request.role()));
        }

        userRepository.save(user);

        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole().name()
        );
    }
    public List<User> search(String email, String name, String role) {
        Specification<User> spec = Specification
                .where(UserSpecification.emailEquals(email))
                .and(UserSpecification.nameContains(name))
                .and(UserSpecification.roleEquals(role));

        return userRepository.findAll(spec);
    }

    public PageResponse<UserResponse> search(
            String email,
            String name,
            String role,
            Pageable pageable
    ) {

        Specification<User> spec = Specification
                .where(UserSpecification.emailEquals(email))
                .and(UserSpecification.nameContains(name))
                .and(UserSpecification.roleEquals(role));

        Page<User> pageResult = userRepository.findAll(spec, pageable);

        List<UserResponse> content = pageResult.getContent()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole().name()
                ))
                .toList();

        return new PageResponse<>(
                content,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }
    
    public boolean existsByEmail(String email) {
        System.out.println("EMAIL DO TOKEN: " + email);
        return userRepository.existsByEmailIgnoreCase(email);
    }

   public User findByEmail(String email) {
    String normalizedEmail = email.trim().toLowerCase();

    System.out.println("🔍 BUSCANDO: [" + normalizedEmail + "]");
    System.out.println("📦 USERS NO BANCO: " + userRepository.findAll());

    return userRepository.findByEmailIgnoreCase(normalizedEmail)
        .orElseThrow(() -> new RuntimeException("User not found for email: " + normalizedEmail));
}

    public void updateAvatar(String avatarUrl) {
        String email = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();

        User user = userRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new RuntimeException("User not found: " + email));

        user.setAvatar(avatarUrl);
        userRepository.save(user);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void delete(Long id) {

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }

        userRepository.deleteById(id);
    }


}
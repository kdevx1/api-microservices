package com.devx.auth.service;

import com.devx.auth.domain.User;
import com.devx.auth.enums.Role;
import com.devx.auth.especification.UserSpecification;
import com.devx.auth.repository.UserRepository;
import com.devx.auth.dto.PageResponse;
import com.devx.auth.dto.UserRequest;
import com.devx.auth.dto.UserResponse;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public UserResponse create(UserRequest request) {

        if (userRepository.existsByEmail(request.email())){
            throw new RuntimeException("Email already exists");
        }

        User user = new User();

        user.setName(request.name()); // 🔥 FALTAVA ISSO
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        Role role;
        try {
            role = request.role() != null
                    ? Role.valueOf(request.role())
                    : Role.ROLE_USER;
        } catch (Exception e) {
            role = Role.ROLE_USER;
        }

        user.setRole(role);

        userRepository.save(user);

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    public List<User> search(String email, String name, String role) {

        List<User> users = userRepository.findAll();

        if (email != null) {
            users = users.stream()
                    .filter(u -> u.getEmail().equalsIgnoreCase(email))
                    .toList();
        }

        if (name != null) {
            users = users.stream()
                    .filter(u -> u.getName().toLowerCase().contains(name.toLowerCase()))
                    .toList();
        }

        if (role != null) {
            users = users.stream()
                    .filter(u -> u.getRole().name().equals(role))
                    .toList();
        }

        return users;
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

        return userRepository.findByEmail(email).isPresent();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void updateAvatar(String email, String avatarUrl) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        user.setAvatar(avatarUrl);

        userRepository.save(user);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

}
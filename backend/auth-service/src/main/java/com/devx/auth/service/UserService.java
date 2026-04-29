package com.devx.auth.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.devx.auth.domain.User;
import com.devx.auth.dto.AuthResponse;
import com.devx.auth.dto.DashboardResponse;
import com.devx.auth.dto.LoginRequest;
import com.devx.auth.dto.PageResponse;
import com.devx.auth.dto.UpdateUserRequest;
import com.devx.auth.dto.UserRequest;
import com.devx.auth.dto.UserResponse;
import com.devx.auth.enums.Role;
import com.devx.auth.especification.UserSpecification;
import com.devx.auth.exception.BusinessException;
import com.devx.auth.repository.UserRepository;
import com.devx.auth.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // =========================
    // LOGIN
    // =========================
    public AuthResponse login(LoginRequest request) {

        User user = findByEmail(request.email());

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new BusinessException("Usuário inativo", 403);
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException("Credenciais inválidas", 401);
        }

        updateLastLogin(user.getEmail());

        String token = jwtService.generateToken(user);

        return new AuthResponse(token, "Bearer");
    }

    // =========================
    // FIND
    // =========================
    public User findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email.trim().toLowerCase())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", 404));
    }

    // =========================
    // CREATE
    // =========================
    public UserResponse create(UserRequest request) {

        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("Email já cadastrado", 409);
        }

        User user = User.builder()
                .name(request.name())
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .role(parseRole(request.role()))
                .active(true)
                .build();

        return map(userRepository.save(user));
    }

    // =========================
    // UPDATE
    // =========================
    public UserResponse update(Long id, UpdateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", 404));

        if (request.name() != null && !request.name().isBlank()) {
            user.setName(request.name());
        }
        if (request.role() != null && !request.role().isBlank()) {
            user.setRole(parseRole(request.role()));
        }
        if (request.active() != null) {
            user.setActive(request.active());
        }

        return map(userRepository.save(user));
    }

    // =========================
    // DELETE (SOFT)
    // =========================
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", 404));

        user.setActive(false);
        userRepository.save(user);
    }

    // =========================
    // TOGGLE ACTIVE
    // =========================
    public UserResponse toggleActive(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", 404));

        user.setActive(!Boolean.TRUE.equals(user.getActive()));

        return map(userRepository.save(user));
    }

    // =========================
    // SEARCH
    // =========================
    public PageResponse<UserResponse> search(
            String email, String name, String role, Boolean active,
            LocalDateTime createdFrom, LocalDateTime createdTo,
            Pageable pageable
    ) {
        Specification<User> spec = Specification
                .where(UserSpecification.emailEquals(email))
                .and(UserSpecification.nameContains(name))
                .and(UserSpecification.roleEquals(role))
                .and(UserSpecification.isActive(active))
                .and(UserSpecification.createdBetween(createdFrom, createdTo));

        Page<User> page = userRepository.findAll(spec, pageable);

        return new PageResponse<>(
                page.getContent().stream().map(this::map).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    // =========================
    // AVATAR
    // =========================
    public void updateAvatar(Long userId, String avatarUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", 404));

        user.setAvatar(avatarUrl);
        userRepository.save(user);
    }

    // =========================
    // LAST LOGIN
    // =========================
    public void updateLastLogin(String email) {
        User user = findByEmail(email);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // =========================
    // DASHBOARD
    // =========================
    public DashboardResponse getDashboard() {
        
        long total = userRepository.count();
        long active = userRepository.countByActiveTrue();
        long admins = userRepository.countByRole(Role.ROLE_ADMIN);

        return new DashboardResponse(total, active, admins);
    }

    // =========================
    // UTILS
    // =========================
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email.trim().toLowerCase());
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public long countUsers() {
        return userRepository.count();
    }

    // =========================
    // INTERNOS
    // =========================
    private Role parseRole(String role) {
        if (role == null || role.isBlank()) return Role.ROLE_USER;
        try {
            return Role.valueOf(role);
        } catch (Exception e) {
            return Role.ROLE_USER;
        }
    }

    public UserResponse map(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getActive(),
                user.getCreatedAt(),
                user.getLastLoginAt()
        );
    }
}
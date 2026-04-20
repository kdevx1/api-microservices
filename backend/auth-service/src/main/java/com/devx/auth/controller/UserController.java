package com.devx.auth.controller;

import com.devx.auth.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import org.springframework.security.access.prepost.PreAuthorize;

import com.devx.auth.dto.UserResponse;
import com.devx.auth.domain.User;
import com.devx.auth.dto.PageResponse;
import com.devx.auth.dto.UpdateUserRequest;
import com.devx.auth.dto.UserRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.UUID;
import java.util.Map;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 🔥 CREATE
    @PreAuthorize("hasAuthority('WRITE')")
    @PostMapping("/register")
    public UserResponse create(@Valid @RequestBody UserRequest request) {
        return userService.create(request);
    }

    // 🔥 LIST (PAGINADO + FILTRO)
    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('READ')")
    @GetMapping
    public PageResponse<UserResponse> search(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String role,
            Pageable pageable
    ) {
        return userService.search(email, name, role, pageable);
    }

    // 🔥 UPDATE
    @PreAuthorize("hasAuthority('ALTER')")
    @PutMapping("/{id}")
    public UserResponse update(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request
    ) {
        return userService.update(id, request);
    }

    // 🔥 DELETE
    @PreAuthorize("hasAuthority('DELETE')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    // 🔥 CURRENT USER
    @GetMapping("/me")
    public User me(Principal principal) {
        return userService.findByEmail(principal.getName());
    }

    // 🔥 AVATAR
    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            Principal principal
    ) throws IOException {

        String email = principal.getName();
        User user = userService.findByEmail(email);

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path path = Paths.get("uploads/" + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        String avatarUrl = "http://localhost:8081/uploads/" + fileName;

        user.setAvatar(avatarUrl);
        userService.save(user);

        return ResponseEntity.ok(Map.of("url", avatarUrl));
    }
}
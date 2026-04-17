package com.devx.auth.controller;

import com.devx.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import com.devx.auth.dto.UserResponse;
import com.devx.auth.domain.User;
import com.devx.auth.dto.PageResponse;
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

    @PostMapping("/register")
    public UserResponse create(@RequestBody UserRequest request) {
        return userService.create(request);
    }

    @GetMapping("/test")
    public String test(Authentication auth) {
        return "User: " + auth.getName();
    }

    // @GetMapping("/me")
    // public String me(Authentication auth) {
    //     return auth.getName() + " | " + auth.getAuthorities();
    // }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<UserResponse> search(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String role,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return userService.search(email, name, role, pageable);
    }

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

        // 🔥 SALVA NO BANCO
        user.setAvatar(avatarUrl);
        userService.save(user);

        return ResponseEntity.ok(Map.of("url", avatarUrl));
    }

    @GetMapping("/me")
    public User getCurrentUser(Principal principal) {
        return userService.findByEmail(principal.getName());
    }
}
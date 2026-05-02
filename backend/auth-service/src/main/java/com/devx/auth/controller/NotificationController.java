package com.devx.auth.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devx.auth.dto.NotificationRequest;
import com.devx.auth.dto.NotificationResponse;
import com.devx.auth.enums.NotificationType;
import com.devx.auth.service.NotificationService;
import com.devx.auth.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> findAll(
            Principal principal,
            Pageable pageable
    ) {
        Long userId = userService.findByEmail(principal.getName()).getId();
        return ResponseEntity.ok(notificationService.findAll(userId, pageable));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> findUnread(Principal principal) {
        Long userId = userService.findByEmail(principal.getName()).getId();
        return ResponseEntity.ok(notificationService.findUnread(userId));
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> countUnread(Principal principal) {
        Long userId = userService.findByEmail(principal.getName()).getId();
        return ResponseEntity.ok(notificationService.countUnread(userId));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(Principal principal) {
        Long userId = userService.findByEmail(principal.getName()).getId();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> create(
            @RequestBody NotificationRequest request,
            Principal principal
    ) {
        Long userId = userService.findByEmail(principal.getName()).getId();
        notificationService.create(userId, request.title(), request.description(), 
            NotificationType.valueOf(request.type()));
        return ResponseEntity.ok().build();
    }
}
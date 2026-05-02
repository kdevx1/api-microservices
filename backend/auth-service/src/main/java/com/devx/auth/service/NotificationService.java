package com.devx.auth.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.devx.auth.domain.Notification;
import com.devx.auth.domain.User;
import com.devx.auth.dto.NotificationResponse;
import com.devx.auth.enums.NotificationType;
import com.devx.auth.exception.BusinessException;
import com.devx.auth.repository.NotificationRepository;
import com.devx.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // =========================
    // FIND
    // =========================
    public Page<NotificationResponse> findAll(Long userId, Pageable pageable) {
        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::map);
    }

    public List<NotificationResponse> findUnread(Long userId) {
        return notificationRepository
                .findByUserIdAndReadFalseOrderByCreatedAtDesc(userId)
                .stream().map(this::map).toList();
    }

    public long countUnread(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    // =========================
    // ACTIONS
    // =========================
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Notificação não encontrada", 404));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    // =========================
    // CREATE (usado internamente)
    // =========================
    public void create(Long userId, String title, String description, NotificationType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", 404));

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .description(description)
                .type(type)
                .read(false)
                .build();

        notificationRepository.save(notification);
    }

    // =========================
    // MAPPER
    // =========================
    private NotificationResponse map(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getTitle(),
                n.getDescription(),
                n.getType().toString(),
                n.getRead(),
                n.getCreatedAt()
        );
    }
}
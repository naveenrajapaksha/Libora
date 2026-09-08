package com.libora.backend.service;

import com.libora.backend.entity.Notification;
import com.libora.backend.entity.User;
import com.libora.backend.repository.NotificationRepository;
import com.libora.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            UserRepository userRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    // =========================
    // CREATE NOTIFICATION
    // =========================

    @Transactional
    public Notification createNotification(
            Long userId,
            String title,
            String message
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with id: " + userId
                        )
                );

        Notification notification = new Notification(
                user,
                title,
                message
        );

        return notificationRepository.save(notification);
    }

    // =========================
    // GET USER NOTIFICATIONS
    // =========================

    public List<Notification> getUserNotifications(
            Long userId
    ) {

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException(
                    "User not found with id: " + userId
            );
        }

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId);
    }

    // =========================
    // MARK AS READ
    // =========================

    @Transactional
    public Notification markAsRead(Long notificationId) {

        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found with id: "
                                                + notificationId
                                )
                        );

        notification.setRead(true);

        return notificationRepository.save(notification);
    }

    // =========================
    // MARK ALL AS READ
    // =========================

    @Transactional
    public void markAllAsRead(Long userId) {

        List<Notification> notifications =
                notificationRepository
                        .findByUserIdOrderByCreatedAtDesc(userId);

        notifications.forEach(notification ->
                notification.setRead(true)
        );

        notificationRepository.saveAll(notifications);
    }
}
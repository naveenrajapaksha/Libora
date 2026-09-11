package com.libora.backend.service;

import com.libora.backend.dto.NotificationResponse;
import com.libora.backend.entity.Notification;
import com.libora.backend.entity.Role;
import com.libora.backend.entity.User;
import com.libora.backend.exception.AccessDeniedException;
import com.libora.backend.exception.ResourceNotFoundException;
import com.libora.backend.repository.NotificationRepository;
import com.libora.backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
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
                        new ResourceNotFoundException(
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

    public List<NotificationResponse> getUserNotifications(
            Long userId,
            Authentication authentication
    ) {

        checkUserAccess(userId, authentication);

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + userId
            );
        }

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationResponse::new)
                .toList();
    }

    // =========================
    // MARK AS READ
    // =========================

    @Transactional
    public NotificationResponse markAsRead(
            Long notificationId,
            Authentication authentication
    ) {

        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found with id: "
                                                + notificationId
                                )
                        );

        checkUserAccess(
                notification.getUser().getId(),
                authentication
        );

        notification.setRead(true);

        Notification savedNotification =
                notificationRepository.save(notification);

        return new NotificationResponse(savedNotification);
    }

    // =========================
    // MARK ALL AS READ
    // =========================

    @Transactional
    public void markAllAsRead(
            Long userId,
            Authentication authentication
    ) {

        checkUserAccess(userId, authentication);

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + userId
            );
        }

        List<Notification> notifications =
                notificationRepository
                        .findByUserIdOrderByCreatedAtDesc(userId);

        notifications.forEach(notification ->
                notification.setRead(true)
        );

        notificationRepository.saveAll(notifications);
    }

    // =========================
    // ACCESS CONTROL
    // =========================

    private void checkUserAccess(
            Long requestedUserId,
            Authentication authentication
    ) {

        User authenticatedUser = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"
                        )
                );

        Role role = authenticatedUser.getRole();

        // Admin and Librarian can access user notifications
        if (role == Role.ADMIN || role == Role.LIBRARIAN) {
            return;
        }

        // Member can access only their own notifications
        if (role == Role.MEMBER &&
                !authenticatedUser.getId().equals(requestedUserId)) {

            throw new AccessDeniedException(
                    "You can only access your own notifications"
            );
        }
    }
}
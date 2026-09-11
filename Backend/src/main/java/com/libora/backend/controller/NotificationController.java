package com.libora.backend.controller;

import com.libora.backend.dto.NotificationResponse;
import com.libora.backend.entity.Notification;
import com.libora.backend.service.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService
    ) {
        this.notificationService = notificationService;
    }

    // =========================
    // GET USER NOTIFICATIONS
    // =========================

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(
            @PathVariable Long userId,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                notificationService.getUserNotifications(
                        userId,
                        authentication
                )
        );
    }

    // =========================
    // MARK NOTIFICATION AS READ
    // =========================

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                notificationService.markAsRead(
                        notificationId,
                        authentication
                )
        );
    }

    // =========================
    // MARK ALL AS READ
    // =========================

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @PathVariable Long userId,
            Authentication authentication
    ) {

        notificationService.markAllAsRead(
                userId,
                authentication
        );

        return ResponseEntity.noContent().build();
    }
}
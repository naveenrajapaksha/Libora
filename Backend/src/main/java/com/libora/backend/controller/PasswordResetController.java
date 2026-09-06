package com.libora.backend.controller;

import com.libora.backend.service.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(
            PasswordResetService passwordResetService
    ) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @RequestParam String email
    ) {
        return ResponseEntity.ok(
                passwordResetService.generateOtp(email)
        );
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(
            @RequestParam String email,
            @RequestParam String otp
    ) {
        return ResponseEntity.ok(
                passwordResetService.verifyOtp(email, otp)
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String email,
            @RequestParam String newPassword
    ) {
        return ResponseEntity.ok(
                passwordResetService.resetPassword(
                        email,
                        newPassword
                )
        );
    }
}
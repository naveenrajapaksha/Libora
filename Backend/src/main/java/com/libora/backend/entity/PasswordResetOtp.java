package com.libora.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_otps")
public class PasswordResetOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String otpHash;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean used = false;

    @Column(nullable = false)
    private int attempts = 0;

    public PasswordResetOtp() {
    }

    public PasswordResetOtp(
            String email,
            String otpHash,
            LocalDateTime expiresAt
    ) {
        this.email = email;
        this.otpHash = otpHash;
        this.expiresAt = expiresAt;
        this.used = false;
        this.attempts = 0;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getOtpHash() {
        return otpHash;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean isUsed() {
        return used;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setOtpHash(String otpHash) {
        this.otpHash = otpHash;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }
}
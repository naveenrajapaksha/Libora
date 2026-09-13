package com.libora.backend.service;

import com.libora.backend.entity.PasswordResetOtp;
import com.libora.backend.entity.User;
import com.libora.backend.repository.PasswordResetOtpRepository;
import com.libora.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetOtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetOtpRepository otpRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // =========================
    // GENERATE OTP
    // =========================

    public String generateOtp(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        otpRepository.deleteByEmail(email);

        String otp = String.format(
                "%06d",
                secureRandom.nextInt(1_000_000)
        );

        String otpHash = passwordEncoder.encode(otp);

        PasswordResetOtp passwordResetOtp =
                new PasswordResetOtp(
                        email,
                        otpHash,
                        LocalDateTime.now().plusMinutes(5)
                );

        otpRepository.save(passwordResetOtp);

        // =========================
        // SEND OTP EMAIL
        // =========================

        String subject = "Libora - Password Reset OTP";

        String message =
                "Hello " + user.getFullName() + ",\n\n"
                        + "We received a request to reset your Libora account password.\n\n"
                        + "Your One-Time Password (OTP) is:\n\n"
                        + otp + "\n\n"
                        + "This OTP is valid for 5 minutes.\n\n"
                        + "If you did not request a password reset, please ignore this email.\n\n"
                        + "Regards,\n"
                        + "Libora Library Management System";

        emailService.sendEmail(
                email,
                subject,
                message
        );

        return "OTP generated successfully.";
    }

    // =========================
    // VERIFY OTP
    // =========================

    public String verifyOtp(
            String email,
            String otp
    ) {

        PasswordResetOtp resetOtp =
                otpRepository.findTopByEmailOrderByIdDesc(email)
                        .orElseThrow(() ->
                                new RuntimeException("OTP not found")
                        );

        if (resetOtp.isUsed()) {
            throw new RuntimeException(
                    "OTP has already been used"
            );
        }

        if (LocalDateTime.now().isAfter(
                resetOtp.getExpiresAt()
        )) {
            throw new RuntimeException(
                    "OTP has expired"
            );
        }

        if (resetOtp.getAttempts() >= 5) {
            throw new RuntimeException(
                    "Too many incorrect OTP attempts"
            );
        }

        resetOtp.setAttempts(
                resetOtp.getAttempts() + 1
        );

        if (!passwordEncoder.matches(
                otp,
                resetOtp.getOtpHash()
        )) {

            otpRepository.save(resetOtp);

            throw new RuntimeException(
                    "Invalid OTP"
            );
        }

        resetOtp.setUsed(true);

        otpRepository.save(resetOtp);

        return "OTP verified successfully.";
    }

    // =========================
    // RESET PASSWORD
    // =========================

    public String resetPassword(
            String email,
            String newPassword
    ) {

        PasswordResetOtp resetOtp =
                otpRepository.findTopByEmailOrderByIdDesc(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Please verify OTP first"
                                )
                        );

        if (!resetOtp.isUsed()) {
            throw new RuntimeException(
                    "Please verify OTP first"
            );
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(user);

        otpRepository.deleteByEmail(email);

        return "Password reset successfully.";
    }
}
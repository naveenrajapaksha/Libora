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

    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetOtpRepository otpRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
    }

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

        // Development purpose only.
        // Later we will send this OTP through email.
        System.out.println(
                "PASSWORD RESET OTP for "
                        + email
                        + " = "
                        + otp
        );

        return "OTP generated successfully.";
    }

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
            throw new RuntimeException("OTP has already been used");
        }

        if (LocalDateTime.now().isAfter(resetOtp.getExpiresAt())) {
            throw new RuntimeException("OTP has expired");
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

            throw new RuntimeException("Invalid OTP");
        }

        resetOtp.setUsed(true);
        otpRepository.save(resetOtp);

        return "OTP verified successfully.";
    }

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
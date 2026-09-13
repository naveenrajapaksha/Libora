package com.libora.backend.service;

import com.libora.backend.dto.LoginRequest;
import com.libora.backend.dto.RegisterRequest;
import com.libora.backend.entity.Role;
import com.libora.backend.entity.User;
import com.libora.backend.entity.UserStatus;
import com.libora.backend.repository.UserRepository;
import com.libora.backend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // =========================
    // REGISTER MEMBER
    // =========================

    public User registerMember(RegisterRequest request) {

        /*
         * Deleted accounts do not block email reuse.
         *
         * ACTIVE / INACTIVE / PENDING / REJECTED
         * accounts with the same email are not allowed.
         */
        if (userRepository.existsByEmailAndStatusNot(
                request.getEmail(),
                UserStatus.DELETED
        )) {
            throw new IllegalArgumentException(
                    "An active account with this email already exists"
            );
        }

        User user = new User(
                request.getFullName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.MEMBER
        );

        user.setStatus(UserStatus.PENDING);

        return userRepository.save(user);
    }

    // =========================
    // LOGIN
    // =========================

    public User login(LoginRequest request) {

        /*
         * Find the latest non-deleted account.
         *
         * This is important because the same email
         * can exist in a DELETED old account and a
         * newly registered account.
         */
        User user = userRepository
                .findFirstByEmailAndStatusNotOrderByIdDesc(
                        request.getEmail(),
                        UserStatus.DELETED
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid email or password"
                        )
                );

        // =========================
        // CHECK PASSWORD
        // =========================

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new IllegalArgumentException(
                    "Invalid email or password"
            );
        }

        // =========================
        // CHECK ACCOUNT STATUS
        // =========================

        if (user.getStatus() == UserStatus.PENDING) {

            throw new IllegalArgumentException(
                    "Your account is pending approval"
            );
        }

        if (user.getStatus() == UserStatus.REJECTED) {

            throw new IllegalArgumentException(
                    "Your account has been rejected"
            );
        }

        if (user.getStatus() == UserStatus.INACTIVE) {

            throw new IllegalArgumentException(
                    "Your account is inactive"
            );
        }

        /*
         * DELETED accounts are excluded by the repository
         * query above, so they cannot reach this point.
         */

        return user;
    }

    // =========================
    // GENERATE JWT TOKEN
    // =========================

    public String generateToken(User user) {

        return jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
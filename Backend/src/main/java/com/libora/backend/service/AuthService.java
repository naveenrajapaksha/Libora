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

    public User registerMember(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "An account with this email already exists"
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

    public User login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid email or password"
                        )
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new IllegalArgumentException(
                    "Invalid email or password"
            );
        }

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

        return user;
    }

    public String generateToken(User user) {

        return jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );
    }
}
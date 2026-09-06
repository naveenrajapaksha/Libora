package com.libora.backend.service;

import com.libora.backend.dto.RegisterRequest;
import com.libora.backend.entity.Role;
import com.libora.backend.entity.User;
import com.libora.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerMember(RegisterRequest request) {

        // Check whether email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "An account with this email already exists"
            );
        }

        // Create new member
        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());

        // Hash the password before saving
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        // Members cannot choose their own role
        user.setRole(Role.MEMBER);

        // New members must be approved
        user.setStatus(
                com.libora.backend.entity.UserStatus.PENDING
        );

        return userRepository.save(user);
    }
}
package com.libora.backend.service;

import com.libora.backend.dto.UserResponse;
import com.libora.backend.entity.User;
import com.libora.backend.entity.UserStatus;
import com.libora.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Get all users
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::toUserResponse)
                .toList();
    }

    // Get user by ID
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found with id: " + id
                        )
                );

        return toUserResponse(user);
    }

    // Approve pending user
    public UserResponse approveUser(Long id) {

        User user = getUserEntityById(id);

        if (user.getStatus() != UserStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Only pending users can be approved"
            );
        }

        user.setStatus(UserStatus.ACTIVE);

        return toUserResponse(
                userRepository.save(user)
        );
    }

    // Reject pending user
    public UserResponse rejectUser(Long id) {

        User user = getUserEntityById(id);

        if (user.getStatus() != UserStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Only pending users can be rejected"
            );
        }

        user.setStatus(UserStatus.REJECTED);

        return toUserResponse(
                userRepository.save(user)
        );
    }

    // Deactivate active user
    public UserResponse deactivateUser(Long id) {

        User user = getUserEntityById(id);

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Only active users can be deactivated"
            );
        }

        user.setStatus(UserStatus.INACTIVE);

        return toUserResponse(
                userRepository.save(user)
        );
    }

    // Activate inactive user
    public UserResponse activateUser(Long id) {

        User user = getUserEntityById(id);

        if (user.getStatus() != UserStatus.INACTIVE) {
            throw new IllegalArgumentException(
                    "Only inactive users can be activated"
            );
        }

        user.setStatus(UserStatus.ACTIVE);

        return toUserResponse(
                userRepository.save(user)
        );
    }

    // Find user entity by ID
    private User getUserEntityById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found with id: " + id
                        )
                );
    }

    // Convert User Entity to UserResponse DTO
    private UserResponse toUserResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus()
        );
    }
}
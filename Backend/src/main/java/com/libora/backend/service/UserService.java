package com.libora.backend.service;

import com.libora.backend.entity.User;
import com.libora.backend.entity.UserStatus;
import com.libora.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found with id: " + id
                        )
                );
    }

    public User approveUser(Long id) {

        User user = getUserById(id);

        if (user.getStatus() != UserStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Only pending users can be approved"
            );
        }

        user.setStatus(UserStatus.ACTIVE);

        return userRepository.save(user);
    }

    public User rejectUser(Long id) {

        User user = getUserById(id);

        if (user.getStatus() != UserStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Only pending users can be rejected"
            );
        }

        user.setStatus(UserStatus.REJECTED);

        return userRepository.save(user);
    }

    public User deactivateUser(Long id) {

        User user = getUserById(id);

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Only active users can be deactivated"
            );
        }

        user.setStatus(UserStatus.INACTIVE);

        return userRepository.save(user);
    }

    public User activateUser(Long id) {

        User user = getUserById(id);

        if (user.getStatus() != UserStatus.INACTIVE) {
            throw new IllegalArgumentException(
                    "Only inactive users can be activated"
            );
        }

        user.setStatus(UserStatus.ACTIVE);

        return userRepository.save(user);
    }
}
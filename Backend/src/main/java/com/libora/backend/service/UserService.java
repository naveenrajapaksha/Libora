package com.libora.backend.service;

import com.libora.backend.dto.UserResponse;
import com.libora.backend.entity.Role;
import com.libora.backend.entity.User;
import com.libora.backend.entity.UserStatus;
import com.libora.backend.exception.ResourceNotFoundException;
import com.libora.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // =========================
    // GET ALL USERS
    // =========================

    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::toUserResponse)
                .toList();
    }

    // =========================
    // GET USER BY ID
    // =========================

    public UserResponse getUserById(Long id) {

        User user = getUserEntityById(id);

        return toUserResponse(user);
    }

    // =========================
    // APPROVE USER
    // =========================

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

    // =========================
    // REJECT USER
    // =========================

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

    // =========================
    // DEACTIVATE USER
    // =========================

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

    // =========================
    // ACTIVATE USER
    // =========================

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

    // =========================
    // DELETE MEMBER
    // =========================

    @Transactional
    public UserResponse deleteMember(Long id) {

        User member = getUserEntityById(id);

        // Make sure the selected account is a MEMBER
        if (member.getRole() != Role.MEMBER) {
            throw new IllegalArgumentException(
                    "Only member accounts can be deleted from this endpoint"
            );
        }

        // Prevent deleting an already deleted account
        if (member.getStatus() == UserStatus.DELETED) {
            throw new IllegalArgumentException(
                    "Member account is already deleted"
            );
        }

        /*
         * SOFT DELETE
         *
         * We do NOT remove the database record.
         * Only the account status is changed to DELETED.
         *
         * Therefore:
         * - Borrow history is preserved
         * - Return history is preserved
         * - Penalty history is preserved
         * - Notification history is preserved
         */
        member.setStatus(UserStatus.DELETED);

        User savedMember =
                userRepository.save(member);

        return toUserResponse(savedMember);
    }

    // =========================
    // FIND USER ENTITY
    // =========================

    private User getUserEntityById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        )
                );
    }

    // =========================
    // CONVERT ENTITY → DTO
    // =========================

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
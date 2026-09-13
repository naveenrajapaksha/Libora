package com.libora.backend.service;

import com.libora.backend.dto.CreateLibrarianRequest;
import com.libora.backend.dto.LibrarianResponse;
import com.libora.backend.entity.Role;
import com.libora.backend.entity.User;
import com.libora.backend.entity.UserStatus;
import com.libora.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LibrarianService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LibrarianService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================
    // CREATE LIBRARIAN
    // =========================

    public LibrarianResponse createLibrarian(
            CreateLibrarianRequest request
    ) {

        /*
         * A deleted account does not block email reuse.
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

        User librarian = new User(
                request.getFullName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.LIBRARIAN
        );

        /*
         * Librarians created by an Admin are immediately active.
         */
        librarian.setStatus(UserStatus.ACTIVE);

        User savedLibrarian =
                userRepository.save(librarian);

        return toLibrarianResponse(savedLibrarian);
    }

    // =========================
    // GET ALL LIBRARIANS
    // =========================

    public List<LibrarianResponse> getAllLibrarians() {

        return userRepository.findAll()
                .stream()
                .filter(user ->
                        user.getRole() == Role.LIBRARIAN
                )
                .filter(user ->
                        user.getStatus() != UserStatus.DELETED
                )
                .map(this::toLibrarianResponse)
                .toList();
    }

    // =========================
    // GET LIBRARIAN BY ID
    // =========================

    public LibrarianResponse getLibrarianById(Long id) {

        User user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Librarian not found with id: "
                                                + id
                                )
                        );

        if (user.getRole() != Role.LIBRARIAN) {
            throw new IllegalArgumentException(
                    "User is not a librarian"
            );
        }

        if (user.getStatus() == UserStatus.DELETED) {
            throw new IllegalArgumentException(
                    "Librarian account has been deleted"
            );
        }

        return toLibrarianResponse(user);
    }

    // =========================
    // ACTIVATE LIBRARIAN
    // =========================

    public LibrarianResponse activateLibrarian(Long id) {

        User librarian =
                getLibrarianEntityById(id);

        if (librarian.getStatus()
                != UserStatus.INACTIVE) {

            throw new IllegalArgumentException(
                    "Only inactive librarians can be activated"
            );
        }

        librarian.setStatus(UserStatus.ACTIVE);

        User savedLibrarian =
                userRepository.save(librarian);

        return toLibrarianResponse(savedLibrarian);
    }

    // =========================
    // DEACTIVATE LIBRARIAN
    // =========================

    public LibrarianResponse deactivateLibrarian(Long id) {

        User librarian =
                getLibrarianEntityById(id);

        if (librarian.getStatus()
                != UserStatus.ACTIVE) {

            throw new IllegalArgumentException(
                    "Only active librarians can be deactivated"
            );
        }

        librarian.setStatus(UserStatus.INACTIVE);

        User savedLibrarian =
                userRepository.save(librarian);

        return toLibrarianResponse(savedLibrarian);
    }

    // =========================
    // DELETE LIBRARIAN
    // =========================

    @Transactional
    public LibrarianResponse deleteLibrarian(Long id) {

        User librarian =
                getLibrarianEntityById(id);

        if (librarian.getStatus()
                == UserStatus.DELETED) {

            throw new IllegalArgumentException(
                    "Librarian account is already deleted"
            );
        }

        /*
         * Soft delete:
         *
         * We do NOT physically delete the database row.
         * This preserves transaction/history records.
         */
        librarian.setStatus(UserStatus.DELETED);

        User savedLibrarian =
                userRepository.save(librarian);

        return toLibrarianResponse(savedLibrarian);
    }

    // =========================
    // FIND LIBRARIAN ENTITY
    // =========================

    private User getLibrarianEntityById(Long id) {

        User user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Librarian not found with id: "
                                                + id
                                )
                        );

        if (user.getRole() != Role.LIBRARIAN) {
            throw new IllegalArgumentException(
                    "User is not a librarian"
            );
        }

        if (user.getStatus() == UserStatus.DELETED) {
            throw new IllegalArgumentException(
                    "Librarian account has been deleted"
            );
        }

        return user;
    }

    // =========================
    // CONVERT ENTITY → DTO
    // =========================

    private LibrarianResponse toLibrarianResponse(
            User user
    ) {

        return new LibrarianResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus()
        );
    }
}
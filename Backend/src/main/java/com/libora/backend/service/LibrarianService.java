package com.libora.backend.service;

import com.libora.backend.dto.CreateLibrarianRequest;
import com.libora.backend.dto.LibrarianResponse;
import com.libora.backend.entity.Role;
import com.libora.backend.entity.User;
import com.libora.backend.entity.UserStatus;
import com.libora.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "An account with this email already exists"
            );
        }

        User librarian = new User(
                request.getFullName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.LIBRARIAN
        );

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

    public void deleteLibrarian(Long id) {

        User librarian =
                getLibrarianEntityById(id);

        userRepository.delete(librarian);
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
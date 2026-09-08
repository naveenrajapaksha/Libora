package com.libora.backend.service;

import com.libora.backend.dto.CreateLibrarianRequest;
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

    // Create a new librarian
    public User createLibrarian(CreateLibrarianRequest request) {

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

        return userRepository.save(librarian);
    }

    // Get all librarians
    public List<User> getAllLibrarians() {

        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == Role.LIBRARIAN)
                .toList();
    }

    // Get librarian by ID
    public User getLibrarianById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Librarian not found with id: " + id
                        )
                );

        if (user.getRole() != Role.LIBRARIAN) {
            throw new IllegalArgumentException(
                    "User is not a librarian"
            );
        }

        return user;
    }

    // Activate librarian
    public User activateLibrarian(Long id) {

        User librarian = getLibrarianById(id);

        if (librarian.getStatus() != UserStatus.INACTIVE) {
            throw new IllegalArgumentException(
                    "Only inactive librarians can be activated"
            );
        }

        librarian.setStatus(UserStatus.ACTIVE);

        return userRepository.save(librarian);
    }

    // Deactivate librarian
    public User deactivateLibrarian(Long id) {

        User librarian = getLibrarianById(id);

        if (librarian.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Only active librarians can be deactivated"
            );
        }

        librarian.setStatus(UserStatus.INACTIVE);

        return userRepository.save(librarian);
    }

    // Delete librarian
    public void deleteLibrarian(Long id) {

        User librarian = getLibrarianById(id);

        userRepository.delete(librarian);
    }
}
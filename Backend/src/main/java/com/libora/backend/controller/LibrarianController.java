package com.libora.backend.controller;

import com.libora.backend.dto.CreateLibrarianRequest;
import com.libora.backend.entity.User;
import com.libora.backend.service.LibrarianService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/librarians")
@SecurityRequirement(name = "bearerAuth")
public class LibrarianController {

    private final LibrarianService librarianService;

    public LibrarianController(LibrarianService librarianService) {
        this.librarianService = librarianService;
    }

    // Create librarian
    @PostMapping
    public ResponseEntity<User> createLibrarian(
            @RequestBody CreateLibrarianRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(librarianService.createLibrarian(request));
    }

    // Get all librarians
    @GetMapping
    public ResponseEntity<List<User>> getAllLibrarians() {

        return ResponseEntity.ok(
                librarianService.getAllLibrarians()
        );
    }

    // Get librarian by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getLibrarianById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                librarianService.getLibrarianById(id)
        );
    }

    // Activate librarian
    @PutMapping("/{id}/activate")
    public ResponseEntity<User> activateLibrarian(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                librarianService.activateLibrarian(id)
        );
    }

    // Deactivate librarian
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<User> deactivateLibrarian(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                librarianService.deactivateLibrarian(id)
        );
    }

    // Delete librarian
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLibrarian(
            @PathVariable Long id
    ) {

        librarianService.deleteLibrarian(id);

        return ResponseEntity.noContent().build();
    }
}
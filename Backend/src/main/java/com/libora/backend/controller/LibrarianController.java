package com.libora.backend.controller;

import com.libora.backend.dto.CreateLibrarianRequest;
import com.libora.backend.dto.LibrarianResponse;
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

    public LibrarianController(
            LibrarianService librarianService
    ) {
        this.librarianService = librarianService;
    }

    // =========================
    // CREATE LIBRARIAN
    // =========================

    @PostMapping
    public ResponseEntity<LibrarianResponse> createLibrarian(
            @RequestBody CreateLibrarianRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        librarianService.createLibrarian(request)
                );
    }

    // =========================
    // GET ALL LIBRARIANS
    // =========================

    @GetMapping
    public ResponseEntity<List<LibrarianResponse>>
    getAllLibrarians() {

        return ResponseEntity.ok(
                librarianService.getAllLibrarians()
        );
    }

    // =========================
    // GET LIBRARIAN BY ID
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<LibrarianResponse>
    getLibrarianById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                librarianService.getLibrarianById(id)
        );
    }

    // =========================
    // ACTIVATE LIBRARIAN
    // =========================

    @PutMapping("/{id}/activate")
    public ResponseEntity<LibrarianResponse>
    activateLibrarian(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                librarianService.activateLibrarian(id)
        );
    }

    // =========================
    // DEACTIVATE LIBRARIAN
    // =========================

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<LibrarianResponse>
    deactivateLibrarian(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                librarianService.deactivateLibrarian(id)
        );
    }

    // =========================
    // DELETE LIBRARIAN
    // =========================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLibrarian(
            @PathVariable Long id
    ) {

        librarianService.deleteLibrarian(id);

        return ResponseEntity.noContent().build();
    }
}
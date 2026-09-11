package com.libora.backend.controller;

import com.libora.backend.dto.LibrarianDashboardResponse;
import com.libora.backend.service.LibrarianDashboardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/librarian/dashboard")
@SecurityRequirement(name = "bearerAuth")
public class LibrarianDashboardController {

    private final LibrarianDashboardService librarianDashboardService;

    public LibrarianDashboardController(
            LibrarianDashboardService librarianDashboardService
    ) {
        this.librarianDashboardService = librarianDashboardService;
    }

    // =========================
    // LIBRARIAN DASHBOARD
    // =========================

    @GetMapping
    public ResponseEntity<LibrarianDashboardResponse> getDashboard() {

        return ResponseEntity.ok(
                librarianDashboardService.getDashboard()
        );
    }
}
package com.libora.backend.controller;

import com.libora.backend.dto.UserResponse;
import com.libora.backend.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // =========================
    // GET ALL USERS
    // =========================

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        return ResponseEntity.ok(
                userService.getAllUsers()
        );
    }

    // =========================
    // GET USER BY ID
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                userService.getUserById(id)
        );
    }

    // =========================
    // APPROVE USER
    // =========================

    @PutMapping("/{id}/approve")
    public ResponseEntity<UserResponse> approveUser(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                userService.approveUser(id)
        );
    }

    // =========================
    // REJECT USER
    // =========================

    @PutMapping("/{id}/reject")
    public ResponseEntity<UserResponse> rejectUser(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                userService.rejectUser(id)
        );
    }

    // =========================
    // DEACTIVATE USER
    // =========================

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<UserResponse> deactivateUser(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                userService.deactivateUser(id)
        );
    }

    // =========================
    // ACTIVATE USER
    // =========================

    @PutMapping("/{id}/activate")
    public ResponseEntity<UserResponse> activateUser(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                userService.activateUser(id)
        );
    }

    // =========================
    // DELETE MEMBER
    // =========================

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponse> deleteMember(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                userService.deleteMember(id)
        );
    }
}
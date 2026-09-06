package com.libora.backend.controller;

import com.libora.backend.dto.RegisterRequest;
import com.libora.backend.dto.RegisterResponse;
import com.libora.backend.entity.User;
import com.libora.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerMember(
            @Valid @RequestBody RegisterRequest request
    ) {

        User registeredUser = authService.registerMember(request);

        RegisterResponse response = new RegisterResponse(
                registeredUser.getId(),
                registeredUser.getFullName(),
                registeredUser.getEmail(),
                registeredUser.getRole(),
                registeredUser.getStatus(),
                "Registration successful. Your account is pending approval."
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
package com.libora.backend.controller;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.libora.backend.entity.User;
import com.libora.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<User> approveUser(@PathVariable Long id) {

        return ResponseEntity.ok(
                userService.approveUser(id)
        );
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<User> rejectUser(@PathVariable Long id) {

        return ResponseEntity.ok(
                userService.rejectUser(id)
        );
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<User> deactivateUser(@PathVariable Long id) {

        return ResponseEntity.ok(
                userService.deactivateUser(id)
        );
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<User> activateUser(@PathVariable Long id) {

        return ResponseEntity.ok(
                userService.activateUser(id)
        );
    }
}
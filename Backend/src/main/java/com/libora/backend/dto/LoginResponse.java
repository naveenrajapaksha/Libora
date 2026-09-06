package com.libora.backend.dto;

import com.libora.backend.entity.Role;
import com.libora.backend.entity.UserStatus;

public class LoginResponse {

    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private UserStatus status;
    private String token;
    private String message;

    public LoginResponse() {
    }

    public LoginResponse(
            Long id,
            String fullName,
            String email,
            Role role,
            UserStatus status,
            String token,
            String message
    ) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.status = status;
        this.token = token;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
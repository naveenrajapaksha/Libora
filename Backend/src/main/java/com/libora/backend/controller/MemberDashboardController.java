package com.libora.backend.controller;

import com.libora.backend.dto.MemberDashboardResponse;
import com.libora.backend.service.MemberDashboardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@SecurityRequirement(name = "bearerAuth")
public class MemberDashboardController {

    private final MemberDashboardService memberDashboardService;

    public MemberDashboardController(
            MemberDashboardService memberDashboardService
    ) {
        this.memberDashboardService = memberDashboardService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<MemberDashboardResponse> getDashboard(
            Authentication authentication
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                memberDashboardService.getDashboard(email)
        );
    }
}
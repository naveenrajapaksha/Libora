package com.libora.backend.controller;

import com.libora.backend.dto.BorrowingAnalyticsResponse;
import com.libora.backend.dto.CategoryAnalyticsResponse;
import com.libora.backend.service.AdminAnalyticsService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/analytics")
@SecurityRequirement(name = "bearerAuth")
public class AdminAnalyticsController {

    private final AdminAnalyticsService adminAnalyticsService;

    public AdminAnalyticsController(
            AdminAnalyticsService adminAnalyticsService
    ) {
        this.adminAnalyticsService = adminAnalyticsService;
    }

    // ==========================================
    // BORROWING ANALYTICS
    // ==========================================

    @GetMapping("/borrowing")
    public ResponseEntity<BorrowingAnalyticsResponse>
    getBorrowingAnalytics() {

        return ResponseEntity.ok(
                adminAnalyticsService.getBorrowingAnalytics()
        );
    }

    // ==========================================
    // CATEGORY ANALYTICS
    // ==========================================

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryAnalyticsResponse>>
    getCategoryAnalytics() {

        return ResponseEntity.ok(
                adminAnalyticsService.getCategoryAnalytics()
        );
    }
}
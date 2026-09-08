package com.libora.backend.controller;

import com.libora.backend.dto.BorrowBookRequest;
import com.libora.backend.dto.TransactionResponse;
import com.libora.backend.service.ReminderService;
import com.libora.backend.service.TransactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;
    private final ReminderService reminderService;

    public TransactionController(
            TransactionService transactionService,
            ReminderService reminderService
    ) {
        this.transactionService = transactionService;
        this.reminderService = reminderService;
    }

    // =========================
    // BORROW BOOK
    // =========================

    @PostMapping("/borrow")
    public ResponseEntity<TransactionResponse> borrowBook(
            @Valid @RequestBody BorrowBookRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        transactionService.borrowBook(request)
                );
    }

    // =========================
    // RETURN BOOK
    // =========================

    @PutMapping("/{id}/return")
    public ResponseEntity<TransactionResponse> returnBook(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                transactionService.returnBook(id)
        );
    }

    // =========================
    // GET ALL TRANSACTIONS
    // =========================

    @GetMapping
    public ResponseEntity<List<TransactionResponse>>
    getAllTransactions() {

        return ResponseEntity.ok(
                transactionService.getAllTransactions()
        );
    }

    // =========================
    // GET TRANSACTION BY ID
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse>
    getTransactionById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                transactionService.getTransactionById(id)
        );
    }

    // =========================
    // GET USER TRANSACTIONS
    // =========================

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionResponse>>
    getUserTransactions(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                transactionService.getUserTransactions(userId)
        );
    }

    // =========================
    // TEST REMINDER
    // =========================
    // Temporary endpoint for testing
    // notification + email

    @PostMapping("/{id}/test-reminder")
    public ResponseEntity<String> testReminder(
            @PathVariable Long id
    ) {

        reminderService.sendTestReminder(id);

        return ResponseEntity.ok(
                "Test reminder sent successfully"
        );
    }
}
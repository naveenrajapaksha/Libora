package com.libora.backend.controller;

import com.libora.backend.dto.BorrowBookRequest;
import com.libora.backend.dto.TransactionResponse;
import com.libora.backend.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(
            TransactionService transactionService
    ) {
        this.transactionService = transactionService;
    }

    // =========================
    // BORROW BOOK
    // =========================

    @Operation(
            summary = "Borrow a book",
            description = "Creates a new book borrowing transaction"
    )
    @PostMapping("/borrow")
    public ResponseEntity<TransactionResponse> borrowBook(
            @Valid @RequestBody BorrowBookRequest request,
            Authentication authentication
    ) {

        TransactionResponse response =
                transactionService.borrowBook(
                        request,
                        authentication
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================
    // RETURN BOOK
    // =========================

    @Operation(
            summary = "Return a borrowed book",
            description = "Returns a borrowed book and calculates the final penalty"
    )
    @PutMapping("/{id}/return")
    public ResponseEntity<TransactionResponse> returnBook(
            @PathVariable Long id,
            Authentication authentication
    ) {

        TransactionResponse response =
                transactionService.returnBook(
                        id,
                        authentication
                );

        return ResponseEntity.ok(response);
    }

    // =========================
    // GET ALL TRANSACTIONS
    // =========================

    @Operation(
            summary = "Get all transactions",
            description = "Returns all borrowing transactions. Accessible to admins and librarians."
    )
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions(
            Authentication authentication
    ) {

        List<TransactionResponse> response =
                transactionService.getAllTransactions(
                        authentication
                );

        return ResponseEntity.ok(response);
    }

    // =========================
    // GET TRANSACTION BY ID
    // =========================

    @Operation(
            summary = "Get transaction by ID",
            description = "Returns a single transaction. Members can access only their own transactions."
    )
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @PathVariable Long id,
            Authentication authentication
    ) {

        TransactionResponse response =
                transactionService.getTransactionById(
                        id,
                        authentication
                );

        return ResponseEntity.ok(response);
    }

    // =========================
    // GET USER TRANSACTIONS
    // =========================

    @Operation(
            summary = "Get user's transactions",
            description = "Returns transaction history for a specific user. Members can access only their own history."
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionResponse>> getUserTransactions(
            @PathVariable Long userId,
            Authentication authentication
    ) {

        List<TransactionResponse> response =
                transactionService.getUserTransactions(
                        userId,
                        authentication
                );

        return ResponseEntity.ok(response);
    }
}
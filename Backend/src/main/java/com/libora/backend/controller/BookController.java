package com.libora.backend.controller;

import com.libora.backend.dto.BookResponse;
import com.libora.backend.dto.CreateBookRequest;
import com.libora.backend.service.BookService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@SecurityRequirement(name = "bearerAuth")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // =========================
    // CREATE BOOK
    // ADMIN + LIBRARIAN
    // =========================

    @PostMapping
    public ResponseEntity<BookResponse> createBook(
            @Valid @RequestBody CreateBookRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookService.createBook(request));
    }

    // =========================
    // GET ALL BOOKS
    // ALL AUTHENTICATED USERS
    // =========================

    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {

        return ResponseEntity.ok(
                bookService.getAllBooks()
        );
    }

    // =========================
    // GET BOOK BY ID
    // ALL AUTHENTICATED USERS
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                bookService.getBookById(id)
        );
    }

    // =========================
    // UPDATE BOOK
    // ADMIN + LIBRARIAN
    // =========================

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody CreateBookRequest request
    ) {

        return ResponseEntity.ok(
                bookService.updateBook(id, request)
        );
    }

    // =========================
    // DELETE BOOK
    // ADMIN + LIBRARIAN
    // =========================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @PathVariable Long id
    ) {

        bookService.deleteBook(id);

        return ResponseEntity.noContent().build();
    }
}
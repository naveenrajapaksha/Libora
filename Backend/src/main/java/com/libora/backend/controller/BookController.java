package com.libora.backend.controller;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.libora.backend.entity.Book;
import com.libora.backend.service.BookService;
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

    // Create Book
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book createdBook = bookService.createBook(book);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdBook);
    }

    // Get All Books
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    // Get Book By ID
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                bookService.getBookById(id)
        );
    }

    // Update Book
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(
            @PathVariable Long id,
            @RequestBody Book book
    ) {
        return ResponseEntity.ok(
                bookService.updateBook(id, book)
        );
    }

    // Delete Book
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @PathVariable Long id
    ) {
        bookService.deleteBook(id);

        return ResponseEntity.noContent().build();
    }
}
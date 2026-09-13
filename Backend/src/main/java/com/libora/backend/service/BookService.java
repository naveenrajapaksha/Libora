package com.libora.backend.service;

import com.libora.backend.dto.BookResponse;
import com.libora.backend.dto.CreateBookRequest;
import com.libora.backend.entity.Book;
import com.libora.backend.exception.ResourceNotFoundException;
import com.libora.backend.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // Create Book
    public BookResponse createBook(CreateBookRequest request) {

        // Check duplicate ISBN
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new IllegalArgumentException(
                    "A book with this ISBN already exists"
            );
        }

        // Validate available quantity
        if (request.getAvailableQuantity() < 0) {
            throw new IllegalArgumentException(
                    "Available quantity cannot be negative"
            );
        }

        if (request.getAvailableQuantity() > request.getQuantity()) {
            throw new IllegalArgumentException(
                    "Available quantity cannot be greater than total quantity"
            );
        }

        Book book = new Book(
                request.getTitle(),
                request.getAuthor(),
                request.getIsbn(),
                request.getCategory(),
                request.getQuantity(),
                request.getAvailableQuantity()
        );

        return new BookResponse(
                bookRepository.save(book)
        );
    }

    // Get All Books
    public List<BookResponse> getAllBooks() {

        return bookRepository.findAll()
                .stream()
                .map(BookResponse::new)
                .toList();
    }

    // Get Book By ID
    public BookResponse getBookById(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Book not found with id: " + id
                        )
                );

        return new BookResponse(book);
    }

    // Update Book
    public BookResponse updateBook(
            Long id,
            CreateBookRequest request
    ) {

        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Book not found with id: " + id
                        )
                );

        // Check duplicate ISBN
        if (!existingBook.getIsbn().equals(request.getIsbn())
                && bookRepository.existsByIsbn(request.getIsbn())) {

            throw new IllegalArgumentException(
                    "A book with this ISBN already exists"
            );
        }

        // Validate available quantity
        if (request.getAvailableQuantity() < 0) {
            throw new IllegalArgumentException(
                    "Available quantity cannot be negative"
            );
        }

        if (request.getAvailableQuantity() > request.getQuantity()) {
            throw new IllegalArgumentException(
                    "Available quantity cannot be greater than total quantity"
            );
        }

        // Calculate currently issued copies
        int issuedCopies =
                existingBook.getQuantity()
                        - existingBook.getAvailableQuantity();

        // New total quantity cannot be less than
        // the number of currently issued copies
        if (request.getQuantity() < issuedCopies) {
            throw new IllegalArgumentException(
                    "Quantity cannot be less than currently issued copies"
            );
        }

        existingBook.setTitle(request.getTitle());
        existingBook.setAuthor(request.getAuthor());
        existingBook.setIsbn(request.getIsbn());
        existingBook.setCategory(request.getCategory());
        existingBook.setQuantity(request.getQuantity());

        /*
         * Preserve currently issued copies.
         *
         * Example:
         * Existing quantity = 10
         * Existing available = 7
         * Issued copies = 3
         *
         * New quantity = 15
         * New available = 15 - 3 = 12
         */
        existingBook.setAvailableQuantity(
                request.getQuantity() - issuedCopies
        );

        return new BookResponse(
                bookRepository.save(existingBook)
        );
    }

    // Delete Book
    public void deleteBook(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Book not found with id: " + id
                        )
                );

        bookRepository.delete(book);
    }
}
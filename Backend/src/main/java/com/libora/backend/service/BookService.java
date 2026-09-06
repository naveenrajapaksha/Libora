package com.libora.backend.service;

import com.libora.backend.entity.Book;
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
    public Book createBook(Book book) {

        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new RuntimeException("A book with this ISBN already exists");
        }

        if (book.getQuantity() == null || book.getQuantity() < 0) {
            throw new RuntimeException("Quantity cannot be negative");
        }

        book.setAvailableQuantity(book.getQuantity());

        return bookRepository.save(book);
    }

    // Get All Books
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Get Book By ID
    public Book getBookById(Long id) {

        return bookRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Book not found with id: " + id)
                );
    }

    // Update Book
    public Book updateBook(Long id, Book updatedBook) {

        Book existingBook = getBookById(id);

        if (!existingBook.getIsbn().equals(updatedBook.getIsbn())
                && bookRepository.existsByIsbn(updatedBook.getIsbn())) {

            throw new RuntimeException(
                    "A book with this ISBN already exists"
            );
        }

        if (updatedBook.getQuantity() == null
                || updatedBook.getQuantity() < 0) {

            throw new RuntimeException(
                    "Quantity cannot be negative"
            );
        }

        int issuedCopies =
                existingBook.getQuantity()
                        - existingBook.getAvailableQuantity();

        if (updatedBook.getQuantity() < issuedCopies) {
            throw new RuntimeException(
                    "Quantity cannot be less than currently issued copies"
            );
        }

        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthor(updatedBook.getAuthor());
        existingBook.setIsbn(updatedBook.getIsbn());
        existingBook.setCategory(updatedBook.getCategory());
        existingBook.setQuantity(updatedBook.getQuantity());

        existingBook.setAvailableQuantity(
                updatedBook.getQuantity() - issuedCopies
        );

        return bookRepository.save(existingBook);
    }

    // Delete Book
    public void deleteBook(Long id) {

        Book book = getBookById(id);

        bookRepository.delete(book);
    }
}
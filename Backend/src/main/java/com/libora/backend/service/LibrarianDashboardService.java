package com.libora.backend.service;

import com.libora.backend.dto.LibrarianDashboardResponse;
import com.libora.backend.entity.Book;
import com.libora.backend.entity.Role;
import com.libora.backend.entity.Transaction;
import com.libora.backend.entity.TransactionStatus;
import com.libora.backend.entity.User;
import com.libora.backend.entity.UserStatus;
import com.libora.backend.repository.BookRepository;
import com.libora.backend.repository.TransactionRepository;
import com.libora.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibrarianDashboardService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public LibrarianDashboardService(
            BookRepository bookRepository,
            UserRepository userRepository,
            TransactionRepository transactionRepository
    ) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public LibrarianDashboardResponse getDashboard() {

        // =========================
        // BOOK STATISTICS
        // =========================

        List<Book> books = bookRepository.findAll();

        long totalBooks = books.size();

        long totalCopies = books.stream()
                .mapToLong(book ->
                        book.getQuantity() != null
                                ? book.getQuantity()
                                : 0
                )
                .sum();

        long availableCopies = books.stream()
                .mapToLong(book ->
                        book.getAvailableQuantity() != null
                                ? book.getAvailableQuantity()
                                : 0
                )
                .sum();

        long borrowedBooks =
                totalCopies - availableCopies;


        // =========================
        // MEMBER STATISTICS
        // =========================

        List<User> users = userRepository.findAll();

        long totalMembers = users.stream()
                .filter(user ->
                        user.getRole() == Role.MEMBER
                )
                .count();

        long activeMembers = users.stream()
                .filter(user ->
                        user.getRole() == Role.MEMBER
                                && user.getStatus() == UserStatus.ACTIVE
                )
                .count();

        long pendingMembers = users.stream()
                .filter(user ->
                        user.getRole() == Role.MEMBER
                                && user.getStatus() == UserStatus.PENDING
                )
                .count();


        // =========================
        // TRANSACTION STATISTICS
        // =========================

        List<Transaction> transactions =
                transactionRepository.findAll();

        long borrowedTransactions = transactions.stream()
                .filter(transaction ->
                        transaction.getStatus()
                                == TransactionStatus.BORROWED
                )
                .count();

        long returnedTransactions = transactions.stream()
                .filter(transaction ->
                        transaction.getStatus()
                                == TransactionStatus.RETURNED
                )
                .count();

        long overdueTransactions = transactions.stream()
                .filter(transaction ->
                        transaction.getStatus()
                                == TransactionStatus.OVERDUE
                )
                .count();


        // =========================
        // PENALTY STATISTICS
        // =========================

        double totalPenalties = transactions.stream()
                .mapToDouble(transaction ->
                        transaction.getPenaltyAmount() != null
                                ? transaction.getPenaltyAmount()
                                : 0.0
                )
                .sum();


        // =========================
        // RESPONSE
        // =========================

        return new LibrarianDashboardResponse(
                totalBooks,
                totalCopies,
                availableCopies,
                borrowedBooks,

                totalMembers,
                activeMembers,
                pendingMembers,

                borrowedTransactions,
                returnedTransactions,
                overdueTransactions,

                totalPenalties
        );
    }
}
package com.libora.backend.service;

import com.libora.backend.dto.AdminDashboardResponse;
import com.libora.backend.entity.Role;
import com.libora.backend.entity.Transaction;
import com.libora.backend.entity.TransactionStatus;
import com.libora.backend.entity.User;
import com.libora.backend.repository.BookRepository;
import com.libora.backend.repository.TransactionRepository;
import com.libora.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminDashboardService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public AdminDashboardService(
            BookRepository bookRepository,
            UserRepository userRepository,
            TransactionRepository transactionRepository
    ) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public AdminDashboardResponse getDashboard() {

        // =========================
        // BOOK STATISTICS
        // =========================

        List<com.libora.backend.entity.Book> books =
                bookRepository.findAll();

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
        // USER STATISTICS
        // =========================

        List<User> users = userRepository.findAll();

        long totalMembers = users.stream()
                .filter(user -> user.getRole() == Role.MEMBER)
                .count();

        long activeMembers = users.stream()
                .filter(user ->
                        user.getRole() == Role.MEMBER
                                && user.getStatus().name().equals("ACTIVE")
                )
                .count();

        long pendingMembers = users.stream()
                .filter(user ->
                        user.getRole() == Role.MEMBER
                                && user.getStatus().name().equals("PENDING")
                )
                .count();

        long inactiveMembers = users.stream()
                .filter(user ->
                        user.getRole() == Role.MEMBER
                                && user.getStatus().name().equals("INACTIVE")
                )
                .count();

        long totalLibrarians = users.stream()
                .filter(user -> user.getRole() == Role.LIBRARIAN)
                .count();

        long activeLibrarians = users.stream()
                .filter(user ->
                        user.getRole() == Role.LIBRARIAN
                                && user.getStatus().name().equals("ACTIVE")
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
        // PENALTY
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

        return new AdminDashboardResponse(
                totalBooks,
                totalCopies,
                availableCopies,
                borrowedBooks,

                totalMembers,
                activeMembers,
                pendingMembers,
                inactiveMembers,

                totalLibrarians,
                activeLibrarians,

                borrowedTransactions,
                returnedTransactions,
                overdueTransactions,

                totalPenalties
        );
    }
}
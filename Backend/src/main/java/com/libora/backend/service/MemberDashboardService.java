package com.libora.backend.service;

import com.libora.backend.dto.MemberBorrowedBookResponse;
import com.libora.backend.dto.MemberDashboardResponse;
import com.libora.backend.entity.Role;
import com.libora.backend.entity.Transaction;
import com.libora.backend.entity.TransactionStatus;
import com.libora.backend.entity.User;
import com.libora.backend.repository.TransactionRepository;
import com.libora.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class MemberDashboardService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public MemberDashboardService(
            UserRepository userRepository,
            TransactionRepository transactionRepository
    ) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public MemberDashboardResponse getDashboard(String email) {

        // Find authenticated user by JWT email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Authenticated user not found"
                        )
                );

        // Defense-in-depth role validation
        if (user.getRole() != Role.MEMBER) {
            throw new RuntimeException(
                    "Only members can access member dashboard"
            );
        }

        List<Transaction> transactions =
                transactionRepository.findByUserId(user.getId());

        long totalBorrowedTransactions =
                transactions.size();

        long activeBorrowedBooks =
                transactions.stream()
                        .filter(transaction ->
                                transaction.getStatus()
                                        == TransactionStatus.BORROWED
                                        ||
                                        transaction.getStatus()
                                                == TransactionStatus.OVERDUE
                        )
                        .count();

        long overdueBooks =
                transactions.stream()
                        .filter(transaction ->
                                transaction.getStatus()
                                        == TransactionStatus.OVERDUE
                        )
                        .count();

        long returnedBooks =
                transactions.stream()
                        .filter(transaction ->
                                transaction.getStatus()
                                        == TransactionStatus.RETURNED
                        )
                        .count();

        double totalPenalties =
                transactions.stream()
                        .mapToDouble(transaction -> {

                            Double penalty =
                                    transaction.getPenaltyAmount();

                            return penalty != null
                                    ? penalty
                                    : 0.0;
                        })
                        .sum();

        // Currently borrowed + overdue books
        List<MemberBorrowedBookResponse> borrowedBooks =
                transactions.stream()
                        .filter(transaction ->
                                transaction.getStatus()
                                        == TransactionStatus.BORROWED
                                        ||
                                        transaction.getStatus()
                                                == TransactionStatus.OVERDUE
                        )
                        .map(this::mapToBorrowedBookResponse)
                        .toList();

        return new MemberDashboardResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                totalBorrowedTransactions,
                activeBorrowedBooks,
                overdueBooks,
                returnedBooks,
                totalPenalties,
                borrowedBooks
        );
    }

    private MemberBorrowedBookResponse mapToBorrowedBookResponse(
            Transaction transaction
    ) {

        LocalDate today = LocalDate.now();

        long daysRemaining =
                ChronoUnit.DAYS.between(
                        today,
                        transaction.getDueDate()
                );

        return new MemberBorrowedBookResponse(
                transaction.getId(),
                transaction.getBook().getId(),
                transaction.getBook().getTitle(),
                transaction.getBook().getAuthor(),
                transaction.getBook().getIsbn(),
                transaction.getBorrowDate(),
                transaction.getDueDate(),
                transaction.getStatus(),
                transaction.getPenaltyAmount() != null
                        ? transaction.getPenaltyAmount()
                        : 0.0,
                daysRemaining
        );
    }
}
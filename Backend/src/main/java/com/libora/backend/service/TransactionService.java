package com.libora.backend.service;

import com.libora.backend.dto.BorrowBookRequest;
import com.libora.backend.dto.TransactionResponse;
import com.libora.backend.entity.Book;
import com.libora.backend.entity.Transaction;
import com.libora.backend.entity.TransactionStatus;
import com.libora.backend.entity.User;
import com.libora.backend.repository.BookRepository;
import com.libora.backend.repository.TransactionRepository;
import com.libora.backend.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // Penalty rate
    private static final double PENALTY_PER_DAY = 10.00;

    public TransactionService(
            TransactionRepository transactionRepository,
            BookRepository bookRepository,
            UserRepository userRepository,
            NotificationService notificationService
    ) {
        this.transactionRepository = transactionRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    // =========================
    // BORROW BOOK
    // =========================

    @Transactional
    public TransactionResponse borrowBook(
            BorrowBookRequest request
    ) {

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Book not found with id: "
                                        + request.getBookId()
                        )
                );

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with id: "
                                        + request.getUserId()
                        )
                );

        // Check user status
        if (!user.getStatus().name().equals("ACTIVE")) {
            throw new RuntimeException(
                    "Only active users can borrow books"
            );
        }

        // Check available copies
        if (book.getAvailableQuantity() <= 0) {
            throw new RuntimeException(
                    "Book is currently unavailable"
            );
        }

        // Check whether user already borrowed this book
        List<Transaction> userTransactions =
                transactionRepository.findByUserId(user.getId());

        boolean alreadyBorrowed =
                userTransactions.stream()
                        .anyMatch(transaction ->
                                transaction.getBook().getId()
                                        .equals(book.getId())
                                        &&
                                        (
                                                transaction.getStatus()
                                                        == TransactionStatus.BORROWED
                                                        ||
                                                        transaction.getStatus()
                                                                == TransactionStatus.OVERDUE
                                        )
                        );

        if (alreadyBorrowed) {
            throw new RuntimeException(
                    "You have already borrowed this book"
            );
        }

        // Borrow date
        LocalDate borrowDate = LocalDate.now();

        // Maximum borrowing period = 14 days
        LocalDate dueDate =
                borrowDate.plusDays(14);

        Transaction transaction = new Transaction(
                book,
                user,
                borrowDate,
                dueDate,
                TransactionStatus.BORROWED
        );

        // New transaction starts with zero penalty
        transaction.setPenaltyAmount(0.0);

        // Decrease available quantity
        book.setAvailableQuantity(
                book.getAvailableQuantity() - 1
        );

        bookRepository.save(book);

        // Save transaction
        Transaction savedTransaction =
                transactionRepository.save(transaction);

        // =========================
        // CREATE BORROW NOTIFICATION
        // =========================

        notificationService.createNotification(
                user.getId(),
                "Book Borrowed Successfully",
                "You have borrowed \""
                        + book.getTitle()
                        + "\". Your due date is "
                        + dueDate
                        + "."
        );

        return new TransactionResponse(
                savedTransaction
        );
    }

    // =========================
    // RETURN BOOK
    // =========================

    @Transactional
    public TransactionResponse returnBook(
            Long transactionId
    ) {

        Transaction transaction =
                transactionRepository.findById(transactionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transaction not found with id: "
                                                + transactionId
                                )
                        );

        if (transaction.getStatus()
                == TransactionStatus.RETURNED) {

            throw new RuntimeException(
                    "This book has already been returned"
            );
        }

        LocalDate returnDate = LocalDate.now();

        // =========================
        // CALCULATE PENALTY
        // =========================

        double penalty = calculatePenalty(
                transaction.getDueDate(),
                returnDate
        );

        transaction.setReturnDate(returnDate);

        transaction.setPenaltyAmount(penalty);

        transaction.setStatus(
                TransactionStatus.RETURNED
        );

        // =========================
        // INCREASE AVAILABLE QUANTITY
        // =========================

        Book book = transaction.getBook();

        book.setAvailableQuantity(
                book.getAvailableQuantity() + 1
        );

        bookRepository.save(book);

        // Save updated transaction
        Transaction updatedTransaction =
                transactionRepository.save(transaction);

        // =========================
        // RETURN NOTIFICATION
        // =========================

        String returnMessage;

        if (penalty > 0) {

            returnMessage =
                    "You have returned \""
                            + book.getTitle()
                            + "\". "
                            + "Your overdue penalty is Rs. "
                            + String.format("%.2f", penalty)
                            + ".";

        } else {

            returnMessage =
                    "You have successfully returned \""
                            + book.getTitle()
                            + "\". "
                            + "No overdue penalty was applied.";
        }

        notificationService.createNotification(
                transaction.getUser().getId(),
                "Book Returned",
                returnMessage
        );

        return new TransactionResponse(
                updatedTransaction
        );
    }

    // =========================
    // PENALTY CALCULATION
    // =========================

    private double calculatePenalty(
            LocalDate dueDate,
            LocalDate returnDate
    ) {

        // Returned on or before due date
        if (!returnDate.isAfter(dueDate)) {
            return 0.0;
        }

        long overdueDays =
                ChronoUnit.DAYS.between(
                        dueDate,
                        returnDate
                );

        return overdueDays * PENALTY_PER_DAY;
    }

    // =========================
    // AUTOMATIC OVERDUE CHECK
    // =========================

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateOverdueTransactions() {

        System.out.println(
                "===== OVERDUE CHECK RUNNING ====="
        );

        LocalDate today = LocalDate.now();

        List<Transaction> borrowedTransactions =
                transactionRepository.findByStatus(
                        TransactionStatus.BORROWED
                );

        for (Transaction transaction :
                borrowedTransactions) {

            // Skip if not overdue
            if (!today.isAfter(
                    transaction.getDueDate()
            )) {
                continue;
            }

            // Calculate overdue days
            long overdueDays =
                    ChronoUnit.DAYS.between(
                            transaction.getDueDate(),
                            today
                    );

            // Calculate current penalty
            double penalty =
                    overdueDays * PENALTY_PER_DAY;

            // Change status to OVERDUE
            transaction.setStatus(
                    TransactionStatus.OVERDUE
            );

            // Update penalty
            transaction.setPenaltyAmount(
                    penalty
            );

            // Save transaction
            transactionRepository.save(transaction);

            // =========================
            // OVERDUE NOTIFICATION
            // =========================

            String bookTitle =
                    transaction.getBook().getTitle();

            Long userId =
                    transaction.getUser().getId();

            String title =
                    "Book Overdue";

            String message =
                    "Your borrowed book \""
                            + bookTitle
                            + "\" is overdue by "
                            + overdueDays
                            + " day(s). "
                            + "Current penalty: Rs. "
                            + String.format(
                            "%.2f",
                            penalty
                    )
                            + ".";

            notificationService.createNotification(
                    userId,
                    title,
                    message
            );

            System.out.println(
                    "Overdue updated: Transaction ID = "
                            + transaction.getId()
                            + ", Penalty = Rs. "
                            + penalty
            );
        }
    }

    // =========================
    // GET ALL TRANSACTIONS
    // =========================

    public List<TransactionResponse> getAllTransactions() {

        return transactionRepository.findAll()
                .stream()
                .map(TransactionResponse::new)
                .toList();
    }

    // =========================
    // GET TRANSACTION BY ID
    // =========================

    public TransactionResponse getTransactionById(
            Long id
    ) {

        Transaction transaction =
                transactionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transaction not found with id: "
                                                + id
                                )
                        );

        return new TransactionResponse(
                transaction
        );
    }

    // =========================
    // GET USER TRANSACTIONS
    // =========================

    public List<TransactionResponse> getUserTransactions(
            Long userId
    ) {

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException(
                    "User not found with id: " + userId
            );
        }

        return transactionRepository
                .findByUserId(userId)
                .stream()
                .map(TransactionResponse::new)
                .toList();
    }
}
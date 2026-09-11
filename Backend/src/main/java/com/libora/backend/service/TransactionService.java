package com.libora.backend.service;

import com.libora.backend.dto.BorrowBookRequest;
import com.libora.backend.dto.TransactionResponse;
import com.libora.backend.entity.Book;
import com.libora.backend.entity.Role;
import com.libora.backend.entity.Transaction;
import com.libora.backend.entity.TransactionStatus;
import com.libora.backend.entity.User;
import com.libora.backend.exception.AccessDeniedException;
import com.libora.backend.exception.ResourceNotFoundException;
import com.libora.backend.repository.BookRepository;
import com.libora.backend.repository.TransactionRepository;
import com.libora.backend.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
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

    // =========================
    // CONSTANTS
    // =========================

    private static final double PENALTY_PER_DAY = 10.00;
    private static final int BORROWING_PERIOD_DAYS = 14;

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
            BorrowBookRequest request,
            Authentication authentication
    ) {

        // =========================
        // AUTHORIZATION CHECK
        // =========================

        User authenticatedUser = getAuthenticatedUser(authentication);

        /*
         * MEMBER can borrow only for their own account.
         *
         * ADMIN / LIBRARIAN can create a borrowing transaction
         * for any active user.
         */
        if (authenticatedUser.getRole() == Role.MEMBER) {

            if (!authenticatedUser.getId().equals(request.getUserId())) {
                throw new AccessDeniedException(
                        "Members can only borrow books for their own account"
                );
            }
        }

        // =========================
        // FIND BOOK
        // =========================

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Book not found with id: "
                                        + request.getBookId()
                        )
                );

        // =========================
        // FIND USER
        // =========================

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: "
                                        + request.getUserId()
                        )
                );

        // =========================
        // CHECK USER STATUS
        // =========================

        if (user.getStatus() == null ||
                !"ACTIVE".equals(user.getStatus().name())) {

            throw new RuntimeException(
                    "Only active users can borrow books"
            );
        }

        // =========================
        // CHECK AVAILABLE COPIES
        // =========================

        if (book.getAvailableQuantity() == null ||
                book.getAvailableQuantity() <= 0) {

            throw new RuntimeException(
                    "Book is currently unavailable"
            );
        }

        // =========================
        // CHECK DUPLICATE BORROW
        // =========================

        List<Transaction> userTransactions =
                transactionRepository.findByUserId(user.getId());

        boolean alreadyBorrowed =
                userTransactions.stream()
                        .anyMatch(transaction ->

                                transaction.getBook() != null
                                        &&
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

        // =========================
        // BORROW DATE
        // =========================

        LocalDate borrowDate = LocalDate.now();

        LocalDate dueDate =
                borrowDate.plusDays(BORROWING_PERIOD_DAYS);

        // =========================
        // CREATE TRANSACTION
        // =========================

        Transaction transaction = new Transaction(
                book,
                user,
                borrowDate,
                dueDate,
                TransactionStatus.BORROWED
        );

        transaction.setPenaltyAmount(0.0);

        // =========================
        // DECREASE AVAILABLE COPIES
        // =========================

        book.setAvailableQuantity(
                book.getAvailableQuantity() - 1
        );

        bookRepository.save(book);

        // =========================
        // SAVE TRANSACTION
        // =========================

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        // =========================
        // BORROW NOTIFICATION
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
            Long transactionId,
            Authentication authentication
    ) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        // =========================
        // FIND TRANSACTION
        // =========================

        Transaction transaction =
                transactionRepository.findById(transactionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction not found with id: "
                                                + transactionId
                                )
                        );

        // =========================
        // TRANSACTION ACCESS CHECK
        // =========================

        checkTransactionAccess(
                transaction,
                authenticatedUser
        );

        // =========================
        // CHECK ALREADY RETURNED
        // =========================

        if (transaction.getStatus()
                == TransactionStatus.RETURNED) {

            throw new RuntimeException(
                    "This book has already been returned"
            );
        }

        // =========================
        // RETURN DATE
        // =========================

        LocalDate returnDate = LocalDate.now();

        // =========================
        // CALCULATE FINAL PENALTY
        // =========================

        double penalty = calculatePenalty(
                transaction.getDueDate(),
                returnDate
        );

        transaction.setReturnDate(returnDate);

        transaction.setPenaltyAmount(
                penalty
        );

        transaction.setStatus(
                TransactionStatus.RETURNED
        );

        // =========================
        // INCREASE AVAILABLE COPIES
        // =========================

        Book book = transaction.getBook();

        if (book != null) {

            int currentAvailable =
                    book.getAvailableQuantity() == null
                            ? 0
                            : book.getAvailableQuantity();

            book.setAvailableQuantity(
                    currentAvailable + 1
            );

            bookRepository.save(book);
        }

        // =========================
        // SAVE TRANSACTION
        // =========================

        Transaction updatedTransaction =
                transactionRepository.save(transaction);

        // =========================
        // RETURN NOTIFICATION
        // =========================

        String bookTitle =
                book != null
                        ? book.getTitle()
                        : "the borrowed book";

        String returnMessage;

        if (penalty > 0) {

            returnMessage =
                    "You have returned \""
                            + bookTitle
                            + "\". "
                            + "Your overdue penalty is Rs. "
                            + String.format("%.2f", penalty)
                            + ".";

        } else {

            returnMessage =
                    "You have successfully returned \""
                            + bookTitle
                            + "\". "
                            + "No overdue penalty was applied.";
        }

        if (transaction.getUser() != null) {

            notificationService.createNotification(
                    transaction.getUser().getId(),
                    "Book Returned",
                    returnMessage
            );
        }

        return new TransactionResponse(
                updatedTransaction
        );
    }

    // =========================
    // AUTOMATIC OVERDUE CHECK
    // =========================

    /*
     * Runs every day at midnight.
     *
     * Important:
     * We check BOTH BORROWED and OVERDUE transactions.
     *
     * This prevents the penalty from freezing after the
     * transaction becomes OVERDUE.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateOverdueTransactions() {

        LocalDate today = LocalDate.now();

        // =========================
        // BORROWED TRANSACTIONS
        // =========================

        List<Transaction> borrowedTransactions =
                transactionRepository.findByStatus(
                        TransactionStatus.BORROWED
                );

        for (Transaction transaction :
                borrowedTransactions) {

            if (transaction.getDueDate() == null) {
                continue;
            }

            // Due date has passed
            if (today.isAfter(transaction.getDueDate())) {

                long overdueDays =
                        ChronoUnit.DAYS.between(
                                transaction.getDueDate(),
                                today
                        );

                double penalty =
                        overdueDays * PENALTY_PER_DAY;

                transaction.setPenaltyAmount(
                        penalty
                );

                transaction.setStatus(
                        TransactionStatus.OVERDUE
                );

                transactionRepository.save(
                        transaction
                );

                // =========================
                // OVERDUE NOTIFICATION
                // =========================

                if (transaction.getUser() != null
                        && transaction.getBook() != null) {

                    String message =
                            "Your borrowed book \""
                                    + transaction.getBook().getTitle()
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
                            transaction.getUser().getId(),
                            "Book Overdue",
                            message
                    );
                }
            }
        }

        // =========================
        // UPDATE EXISTING OVERDUE
        // =========================

        List<Transaction> overdueTransactions =
                transactionRepository.findByStatus(
                        TransactionStatus.OVERDUE
                );

        for (Transaction transaction :
                overdueTransactions) {

            if (transaction.getDueDate() == null) {
                continue;
            }

            long overdueDays =
                    ChronoUnit.DAYS.between(
                            transaction.getDueDate(),
                            today
                    );

            if (overdueDays <= 0) {
                continue;
            }

            double penalty =
                    overdueDays * PENALTY_PER_DAY;

            transaction.setPenaltyAmount(
                    penalty
            );

            transactionRepository.save(
                    transaction
            );
        }
    }

    // =========================
    // PENALTY CALCULATION
    // =========================

    private double calculatePenalty(
            LocalDate dueDate,
            LocalDate returnDate
    ) {

        if (dueDate == null ||
                returnDate == null) {

            return 0.0;
        }

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
    // GET ALL TRANSACTIONS
    // =========================

    public List<TransactionResponse> getAllTransactions(
            Authentication authentication
    ) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        checkAdminOrLibrarian(
                authenticatedUser
        );

        return transactionRepository.findAll()
                .stream()
                .map(TransactionResponse::new)
                .toList();
    }

    // =========================
    // GET TRANSACTION BY ID
    // =========================

    public TransactionResponse getTransactionById(
            Long id,
            Authentication authentication
    ) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        Transaction transaction =
                transactionRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction not found with id: "
                                                + id
                                )
                        );

        // =========================
        // OWNERSHIP / ROLE CHECK
        // =========================

        checkTransactionAccess(
                transaction,
                authenticatedUser
        );

        return new TransactionResponse(
                transaction
        );
    }

    // =========================
    // GET USER TRANSACTIONS
    // =========================

    public List<TransactionResponse> getUserTransactions(
            Long userId,
            Authentication authentication
    ) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        // =========================
        // USER EXISTENCE CHECK
        // =========================

        if (!userRepository.existsById(userId)) {

            throw new ResourceNotFoundException(
                    "User not found with id: "
                            + userId
            );
        }

        // =========================
        // OWNERSHIP CHECK
        // =========================

        if (authenticatedUser.getRole() == Role.MEMBER
                &&
                !authenticatedUser.getId().equals(userId)) {

            throw new AccessDeniedException(
                    "You can only view your own transactions"
            );
        }

        return transactionRepository
                .findByUserId(userId)
                .stream()
                .map(TransactionResponse::new)
                .toList();
    }

    // =========================
    // GET AUTHENTICATED USER
    // =========================

    private User getAuthenticatedUser(
            Authentication authentication
    ) {

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication.getName() == null) {

            throw new AccessDeniedException(
                    "Authentication is required"
            );
        }

        return userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"
                        )
                );
    }

    // =========================
    // ADMIN / LIBRARIAN CHECK
    // =========================

    private void checkAdminOrLibrarian(
            User user
    ) {

        if (user.getRole() != Role.ADMIN
                &&
                user.getRole() != Role.LIBRARIAN) {

            throw new AccessDeniedException(
                    "Only administrators and librarians can access all transactions"
            );
        }
    }

    // =========================
    // TRANSACTION ACCESS CHECK
    // =========================

    private void checkTransactionAccess(
            Transaction transaction,
            User authenticatedUser
    ) {

        // ADMIN can access everything
        if (authenticatedUser.getRole()
                == Role.ADMIN) {

            return;
        }

        // LIBRARIAN can access everything
        if (authenticatedUser.getRole()
                == Role.LIBRARIAN) {

            return;
        }

        // MEMBER can access only own transaction
        if (authenticatedUser.getRole()
                == Role.MEMBER) {

            if (transaction.getUser() == null ||
                    !authenticatedUser.getId()
                            .equals(
                                    transaction.getUser().getId()
                            )) {

                throw new AccessDeniedException(
                        "You can only access your own transactions"
                );
            }

            return;
        }

        throw new AccessDeniedException(
                "You do not have permission to access this transaction"
        );
    }
}
package com.libora.backend.dto;

import com.libora.backend.entity.Transaction;

import java.time.LocalDate;

public class TransactionResponse {

    private Long id;

    private Long bookId;
    private String bookTitle;

    private Long userId;
    private String userName;
    private String userEmail;

    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    private String status;

    // Penalty
    private Double penaltyAmount;

    public TransactionResponse(Transaction transaction) {

        this.id = transaction.getId();

        this.bookId = transaction.getBook().getId();
        this.bookTitle = transaction.getBook().getTitle();

        this.userId = transaction.getUser().getId();
        this.userName = transaction.getUser().getFullName();
        this.userEmail = transaction.getUser().getEmail();

        this.borrowDate = transaction.getBorrowDate();
        this.dueDate = transaction.getDueDate();
        this.returnDate = transaction.getReturnDate();

        this.status = transaction.getStatus().name();

        this.penaltyAmount = transaction.getPenaltyAmount();
    }

    public Long getId() {
        return id;
    }

    public Long getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public String getStatus() {
        return status;
    }

    public Double getPenaltyAmount() {
        return penaltyAmount;
    }
}
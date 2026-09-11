package com.libora.backend.dto;

import com.libora.backend.entity.TransactionStatus;

import java.time.LocalDate;

public class MemberBorrowedBookResponse {

    private Long transactionId;
    private Long bookId;

    private String bookTitle;
    private String author;
    private String isbn;

    private LocalDate borrowDate;
    private LocalDate dueDate;

    private TransactionStatus status;

    private double penaltyAmount;

    private long daysRemaining;

    public MemberBorrowedBookResponse() {
    }

    public MemberBorrowedBookResponse(
            Long transactionId,
            Long bookId,
            String bookTitle,
            String author,
            String isbn,
            LocalDate borrowDate,
            LocalDate dueDate,
            TransactionStatus status,
            double penaltyAmount,
            long daysRemaining
    ) {
        this.transactionId = transactionId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.author = author;
        this.isbn = isbn;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = status;
        this.penaltyAmount = penaltyAmount;
        this.daysRemaining = daysRemaining;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public double getPenaltyAmount() {
        return penaltyAmount;
    }

    public void setPenaltyAmount(double penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }

    public long getDaysRemaining() {
        return daysRemaining;
    }

    public void setDaysRemaining(long daysRemaining) {
        this.daysRemaining = daysRemaining;
    }
}
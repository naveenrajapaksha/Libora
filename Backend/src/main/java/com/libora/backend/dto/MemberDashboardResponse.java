package com.libora.backend.dto;

import java.util.List;

public class MemberDashboardResponse {

    private Long memberId;
    private String fullName;
    private String email;

    private long totalBorrowedTransactions;
    private long activeBorrowedBooks;
    private long overdueBooks;
    private long returnedBooks;

    private double totalPenalties;

    private List<MemberBorrowedBookResponse> borrowedBooks;

    public MemberDashboardResponse() {
    }

    public MemberDashboardResponse(
            Long memberId,
            String fullName,
            String email,
            long totalBorrowedTransactions,
            long activeBorrowedBooks,
            long overdueBooks,
            long returnedBooks,
            double totalPenalties,
            List<MemberBorrowedBookResponse> borrowedBooks
    ) {
        this.memberId = memberId;
        this.fullName = fullName;
        this.email = email;
        this.totalBorrowedTransactions = totalBorrowedTransactions;
        this.activeBorrowedBooks = activeBorrowedBooks;
        this.overdueBooks = overdueBooks;
        this.returnedBooks = returnedBooks;
        this.totalPenalties = totalPenalties;
        this.borrowedBooks = borrowedBooks;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getTotalBorrowedTransactions() {
        return totalBorrowedTransactions;
    }

    public void setTotalBorrowedTransactions(long totalBorrowedTransactions) {
        this.totalBorrowedTransactions = totalBorrowedTransactions;
    }

    public long getActiveBorrowedBooks() {
        return activeBorrowedBooks;
    }

    public void setActiveBorrowedBooks(long activeBorrowedBooks) {
        this.activeBorrowedBooks = activeBorrowedBooks;
    }

    public long getOverdueBooks() {
        return overdueBooks;
    }

    public void setOverdueBooks(long overdueBooks) {
        this.overdueBooks = overdueBooks;
    }

    public long getReturnedBooks() {
        return returnedBooks;
    }

    public void setReturnedBooks(long returnedBooks) {
        this.returnedBooks = returnedBooks;
    }

    public double getTotalPenalties() {
        return totalPenalties;
    }

    public void setTotalPenalties(double totalPenalties) {
        this.totalPenalties = totalPenalties;
    }

    public List<MemberBorrowedBookResponse> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void setBorrowedBooks(
            List<MemberBorrowedBookResponse> borrowedBooks
    ) {
        this.borrowedBooks = borrowedBooks;
    }
}
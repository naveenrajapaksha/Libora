package com.libora.backend.dto;

public class LibrarianDashboardResponse {

    private long totalBooks;
    private long totalCopies;
    private long availableCopies;
    private long borrowedBooks;

    private long totalMembers;
    private long activeMembers;
    private long pendingMembers;

    private long borrowedTransactions;
    private long returnedTransactions;
    private long overdueTransactions;

    private double totalPenalties;

    public LibrarianDashboardResponse() {
    }

    public LibrarianDashboardResponse(
            long totalBooks,
            long totalCopies,
            long availableCopies,
            long borrowedBooks,

            long totalMembers,
            long activeMembers,
            long pendingMembers,

            long borrowedTransactions,
            long returnedTransactions,
            long overdueTransactions,

            double totalPenalties
    ) {
        this.totalBooks = totalBooks;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.borrowedBooks = borrowedBooks;

        this.totalMembers = totalMembers;
        this.activeMembers = activeMembers;
        this.pendingMembers = pendingMembers;

        this.borrowedTransactions = borrowedTransactions;
        this.returnedTransactions = returnedTransactions;
        this.overdueTransactions = overdueTransactions;

        this.totalPenalties = totalPenalties;
    }

    public long getTotalBooks() {
        return totalBooks;
    }

    public void setTotalBooks(long totalBooks) {
        this.totalBooks = totalBooks;
    }

    public long getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(long totalCopies) {
        this.totalCopies = totalCopies;
    }

    public long getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(long availableCopies) {
        this.availableCopies = availableCopies;
    }

    public long getBorrowedBooks() {
        return borrowedBooks;
    }

    public void setBorrowedBooks(long borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }

    public long getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(long totalMembers) {
        this.totalMembers = totalMembers;
    }

    public long getActiveMembers() {
        return activeMembers;
    }

    public void setActiveMembers(long activeMembers) {
        this.activeMembers = activeMembers;
    }

    public long getPendingMembers() {
        return pendingMembers;
    }

    public void setPendingMembers(long pendingMembers) {
        this.pendingMembers = pendingMembers;
    }

    public long getBorrowedTransactions() {
        return borrowedTransactions;
    }

    public void setBorrowedTransactions(long borrowedTransactions) {
        this.borrowedTransactions = borrowedTransactions;
    }

    public long getReturnedTransactions() {
        return returnedTransactions;
    }

    public void setReturnedTransactions(long returnedTransactions) {
        this.returnedTransactions = returnedTransactions;
    }

    public long getOverdueTransactions() {
        return overdueTransactions;
    }

    public void setOverdueTransactions(long overdueTransactions) {
        this.overdueTransactions = overdueTransactions;
    }

    public double getTotalPenalties() {
        return totalPenalties;
    }

    public void setTotalPenalties(double totalPenalties) {
        this.totalPenalties = totalPenalties;
    }
}
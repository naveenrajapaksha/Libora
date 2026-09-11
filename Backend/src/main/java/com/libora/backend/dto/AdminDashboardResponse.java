package com.libora.backend.dto;

public class AdminDashboardResponse {

    private long totalBooks;
    private long totalCopies;
    private long availableCopies;
    private long borrowedBooks;

    private long totalMembers;
    private long activeMembers;
    private long pendingMembers;
    private long inactiveMembers;

    private long totalLibrarians;
    private long activeLibrarians;

    private long borrowedTransactions;
    private long returnedTransactions;
    private long overdueTransactions;

    private double totalPenalties;

    public AdminDashboardResponse() {
    }

    public AdminDashboardResponse(
            long totalBooks,
            long totalCopies,
            long availableCopies,
            long borrowedBooks,
            long totalMembers,
            long activeMembers,
            long pendingMembers,
            long inactiveMembers,
            long totalLibrarians,
            long activeLibrarians,
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
        this.inactiveMembers = inactiveMembers;
        this.totalLibrarians = totalLibrarians;
        this.activeLibrarians = activeLibrarians;
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

    public long getInactiveMembers() {
        return inactiveMembers;
    }

    public void setInactiveMembers(long inactiveMembers) {
        this.inactiveMembers = inactiveMembers;
    }

    public long getTotalLibrarians() {
        return totalLibrarians;
    }

    public void setTotalLibrarians(long totalLibrarians) {
        this.totalLibrarians = totalLibrarians;
    }

    public long getActiveLibrarians() {
        return activeLibrarians;
    }

    public void setActiveLibrarians(long activeLibrarians) {
        this.activeLibrarians = activeLibrarians;
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
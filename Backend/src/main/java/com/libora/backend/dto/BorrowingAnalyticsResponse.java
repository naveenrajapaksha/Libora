package com.libora.backend.dto;

import java.util.List;

public class BorrowingAnalyticsResponse {

    private List<MonthlyBorrowingData> monthlyData;
    private long totalBorrowed;
    private long totalReturned;
    private long totalOverdue;

    public BorrowingAnalyticsResponse() {
    }

    public BorrowingAnalyticsResponse(
            List<MonthlyBorrowingData> monthlyData,
            long totalBorrowed,
            long totalReturned,
            long totalOverdue
    ) {
        this.monthlyData = monthlyData;
        this.totalBorrowed = totalBorrowed;
        this.totalReturned = totalReturned;
        this.totalOverdue = totalOverdue;
    }

    public List<MonthlyBorrowingData> getMonthlyData() {
        return monthlyData;
    }

    public void setMonthlyData(List<MonthlyBorrowingData> monthlyData) {
        this.monthlyData = monthlyData;
    }

    public long getTotalBorrowed() {
        return totalBorrowed;
    }

    public void setTotalBorrowed(long totalBorrowed) {
        this.totalBorrowed = totalBorrowed;
    }

    public long getTotalReturned() {
        return totalReturned;
    }

    public void setTotalReturned(long totalReturned) {
        this.totalReturned = totalReturned;
    }

    public long getTotalOverdue() {
        return totalOverdue;
    }

    public void setTotalOverdue(long totalOverdue) {
        this.totalOverdue = totalOverdue;
    }

    public static class MonthlyBorrowingData {

        private String month;
        private long borrowed;
        private long returned;
        private long overdue;

        public MonthlyBorrowingData() {
        }

        public MonthlyBorrowingData(
                String month,
                long borrowed,
                long returned,
                long overdue
        ) {
            this.month = month;
            this.borrowed = borrowed;
            this.returned = returned;
            this.overdue = overdue;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public long getBorrowed() {
            return borrowed;
        }

        public void setBorrowed(long borrowed) {
            this.borrowed = borrowed;
        }

        public long getReturned() {
            return returned;
        }

        public void setReturned(long returned) {
            this.returned = returned;
        }

        public long getOverdue() {
            return overdue;
        }

        public void setOverdue(long overdue) {
            this.overdue = overdue;
        }
    }
}
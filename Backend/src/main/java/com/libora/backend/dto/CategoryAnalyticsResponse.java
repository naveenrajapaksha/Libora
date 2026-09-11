package com.libora.backend.dto;

public class CategoryAnalyticsResponse {

    private String category;
    private long borrowedCount;

    public CategoryAnalyticsResponse() {
    }

    public CategoryAnalyticsResponse(
            String category,
            long borrowedCount
    ) {
        this.category = category;
        this.borrowedCount = borrowedCount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getBorrowedCount() {
        return borrowedCount;
    }

    public void setBorrowedCount(long borrowedCount) {
        this.borrowedCount = borrowedCount;
    }
}
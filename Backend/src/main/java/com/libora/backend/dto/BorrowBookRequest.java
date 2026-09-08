package com.libora.backend.dto;

import jakarta.validation.constraints.NotNull;

public class BorrowBookRequest {

    @NotNull
    private Long bookId;

    @NotNull
    private Long userId;

    public BorrowBookRequest() {
    }

    public BorrowBookRequest(Long bookId, Long userId) {
        this.bookId = bookId;
        this.userId = userId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
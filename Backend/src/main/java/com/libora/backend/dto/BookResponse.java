package com.libora.backend.dto;

import com.libora.backend.entity.Book;

public class BookResponse {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private Integer quantity;
    private Integer availableQuantity;

    public BookResponse() {
    }

    public BookResponse(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.isbn = book.getIsbn();
        this.category = book.getCategory();
        this.quantity = book.getQuantity();
        this.availableQuantity = book.getAvailableQuantity();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getCategory() {
        return category;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }
}
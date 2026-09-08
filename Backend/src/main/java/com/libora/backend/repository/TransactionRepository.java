package com.libora.backend.repository;

import com.libora.backend.entity.Transaction;
import com.libora.backend.entity.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserId(Long userId);

    List<Transaction> findByBookId(Long bookId);

    List<Transaction> findByStatus(TransactionStatus status);
}
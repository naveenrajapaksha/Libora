package com.libora.backend.service;

import com.libora.backend.dto.BorrowingAnalyticsResponse;
import com.libora.backend.dto.CategoryAnalyticsResponse;
import com.libora.backend.entity.Book;
import com.libora.backend.entity.Transaction;
import com.libora.backend.entity.TransactionStatus;
import com.libora.backend.repository.BookRepository;
import com.libora.backend.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminAnalyticsService {

    private final TransactionRepository transactionRepository;
    private final BookRepository bookRepository;

    public AdminAnalyticsService(
            TransactionRepository transactionRepository,
            BookRepository bookRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.bookRepository = bookRepository;
    }

    // ==========================================
    // BORROWING ANALYTICS
    // ==========================================

    public BorrowingAnalyticsResponse getBorrowingAnalytics() {

        List<Transaction> transactions =
                transactionRepository.findAll();

        long totalBorrowed =
                transactions.size();

        long totalReturned =
                transactions.stream()
                        .filter(transaction ->
                                transaction.getStatus()
                                        == TransactionStatus.RETURNED
                        )
                        .count();

        long totalOverdue =
                transactions.stream()
                        .filter(transaction ->
                                transaction.getStatus()
                                        == TransactionStatus.OVERDUE
                        )
                        .count();

        LocalDate today = LocalDate.now();

        // Last 12 months
        YearMonth currentMonth =
                YearMonth.from(today);

        List<BorrowingAnalyticsResponse.MonthlyBorrowingData>
                monthlyData = new ArrayList<>();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM");

        for (int i = 11; i >= 0; i--) {

            YearMonth month =
                    currentMonth.minusMonths(i);

            String monthLabel =
                    month.format(formatter);

            long borrowed =
                    transactions.stream()
                            .filter(transaction ->
                                    transaction.getBorrowDate() != null
                            )
                            .filter(transaction ->
                                    YearMonth.from(
                                            transaction.getBorrowDate()
                                    ).equals(month)
                            )
                            .count();

            long returned =
                    transactions.stream()
                            .filter(transaction ->
                                    transaction.getReturnDate() != null
                            )
                            .filter(transaction ->
                                    YearMonth.from(
                                            transaction.getReturnDate()
                                    ).equals(month)
                            )
                            .count();

            long overdue =
                    transactions.stream()
                            .filter(transaction ->
                                    transaction.getStatus()
                                            == TransactionStatus.OVERDUE
                            )
                            .filter(transaction ->
                                    transaction.getDueDate() != null
                            )
                            .filter(transaction ->
                                    YearMonth.from(
                                            transaction.getDueDate()
                                    ).equals(month)
                            )
                            .count();

            monthlyData.add(
                    new BorrowingAnalyticsResponse.MonthlyBorrowingData(
                            monthLabel,
                            borrowed,
                            returned,
                            overdue
                    )
            );
        }

        return new BorrowingAnalyticsResponse(
                monthlyData,
                totalBorrowed,
                totalReturned,
                totalOverdue
        );
    }

    // ==========================================
    // CATEGORY ANALYTICS
    // ==========================================

    public List<CategoryAnalyticsResponse>
    getCategoryAnalytics() {

        /*
         * Get all books so that categories with
         * zero borrowing transactions are also included.
         */
        List<Book> books =
                bookRepository.findAll();

        /*
         * Get all transactions to calculate
         * borrowing count per category.
         */
        List<Transaction> transactions =
                transactionRepository.findAll();

        /*
         * First create all existing categories
         * with a default count of zero.
         */
        Map<String, Long> categoryCounts =
                books.stream()
                        .filter(book ->
                                book.getCategory() != null
                        )
                        .filter(book ->
                                !book.getCategory()
                                        .trim()
                                        .isEmpty()
                        )
                        .map(book ->
                                book.getCategory().trim()
                        )
                        .distinct()
                        .collect(
                                Collectors.toMap(
                                        category -> category,
                                        category -> 0L,
                                        Long::sum,
                                        LinkedHashMap::new
                                )
                        );

        /*
         * Count borrowing transactions according
         * to each book's category.
         */
        transactions.stream()
                .filter(transaction ->
                        transaction.getBook() != null
                )
                .filter(transaction ->
                        transaction.getBook().getCategory() != null
                )
                .map(transaction ->
                        transaction.getBook()
                                .getCategory()
                                .trim()
                )
                .filter(category ->
                        !category.isEmpty()
                )
                .forEach(category ->
                        categoryCounts.merge(
                                category,
                                1L,
                                Long::sum
                        )
                );

        /*
         * Convert category map into response DTOs
         * and sort by highest borrowing count.
         *
         * If two categories have the same count,
         * sort them alphabetically.
         */
        return categoryCounts.entrySet()
                .stream()
                .map(entry ->
                        new CategoryAnalyticsResponse(
                                entry.getKey(),
                                entry.getValue()
                        )
                )
                .sorted(
                        Comparator
                                .comparing(
                                        CategoryAnalyticsResponse
                                                ::getBorrowedCount
                                )
                                .reversed()
                                .thenComparing(
                                        CategoryAnalyticsResponse
                                                ::getCategory
                                )
                )
                .toList();
    }
}
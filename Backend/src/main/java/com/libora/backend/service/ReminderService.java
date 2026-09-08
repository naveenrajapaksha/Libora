package com.libora.backend.service;

import com.libora.backend.entity.Transaction;
import com.libora.backend.entity.TransactionStatus;
import com.libora.backend.repository.TransactionRepository;
import com.libora.backend.repository.NotificationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReminderService {

    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    public ReminderService(
            TransactionRepository transactionRepository,
            NotificationService notificationService,
            NotificationRepository notificationRepository,
            EmailService emailService
    ) {
        this.transactionRepository = transactionRepository;
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }

    // =========================
    // CHECK DUE DATE REMINDERS
    // =========================

    // TESTING ONLY
    // Runs every 60 seconds
    @Scheduled(cron = "0 0 9 * * *")
    public void sendDueDateReminders() {

        System.out.println(
                "===== DUE DATE REMINDER CHECK RUNNING ====="
        );

        LocalDate today = LocalDate.now();

        // Reminder date = 2 days before due date
        LocalDate reminderDate = today.plusDays(2);

        System.out.println(
                "Today: " + today
        );

        System.out.println(
                "Reminder Date: " + reminderDate
        );

        List<Transaction> transactions =
                transactionRepository.findAll();

        for (Transaction transaction : transactions) {

            // =========================
            // ONLY BORROWED BOOKS
            // =========================

            if (transaction.getStatus()
                    != TransactionStatus.BORROWED) {

                continue;
            }

            // =========================
            // CHECK DUE DATE
            // =========================

            if (!transaction.getDueDate()
                    .equals(reminderDate)) {

                continue;
            }

            String bookTitle =
                    transaction.getBook().getTitle();

            Long userId =
                    transaction.getUser().getId();

            String userEmail =
                    transaction.getUser().getEmail();

            String title =
                    "Book Due Soon";

            String message =
                    "Reminder: Your borrowed book \""
                            + bookTitle
                            + "\" is due on "
                            + transaction.getDueDate()
                            + ". Please return it on time to avoid a penalty.";

            System.out.println(
                    "===== DUE DATE MATCH FOUND ====="
            );

            System.out.println(
                    "Transaction ID: "
                            + transaction.getId()
            );

            System.out.println(
                    "Book: "
                            + bookTitle
            );

            System.out.println(
                    "User ID: "
                            + userId
            );

            System.out.println(
                    "Email: "
                            + userEmail
            );

            // =========================
            // PREVENT DUPLICATE
            // =========================

            boolean alreadySent =
                    notificationRepository
                            .existsByUserIdAndTitleAndMessage(
                                    userId,
                                    title,
                                    message
                            );

            if (alreadySent) {

                System.out.println(
                        "Reminder already sent. Skipping..."
                );

                continue;
            }

            // =========================
            // IN-APP NOTIFICATION
            // =========================

            notificationService.createNotification(
                    userId,
                    title,
                    message
            );

            System.out.println(
                    "In-app notification created."
            );

            // =========================
            // EMAIL NOTIFICATION
            // =========================

            try {

                System.out.println(
                        "===== SENDING EMAIL ====="
                );

                System.out.println(
                        "To: " + userEmail
                );

                System.out.println(
                        "Subject: " + title
                );

                emailService.sendEmail(
                        userEmail,
                        title,
                        message
                );

                System.out.println(
                        "===== EMAIL SENT SUCCESSFULLY ====="
                );

            } catch (Exception e) {

                System.out.println(
                        "===== EMAIL SENDING FAILED ====="
                );

                System.out.println(
                        "Error: " + e.getMessage()
                );

                e.printStackTrace();
            }
        }
    }

    // =========================
    // TEST REMINDER
    // =========================

    // This method is only for manual testing.
    // Remove this method after email testing is completed.

    public void sendTestReminder(Long transactionId) {

        System.out.println(
                "===== TEST REMINDER STARTED ====="
        );

        Transaction transaction =
                transactionRepository.findById(transactionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transaction not found with id: "
                                                + transactionId
                                )
                        );

        // =========================
        // CHECK TRANSACTION STATUS
        // =========================

        if (transaction.getStatus()
                != TransactionStatus.BORROWED) {

            throw new RuntimeException(
                    "This transaction is not currently borrowed"
            );
        }

        Long userId =
                transaction.getUser().getId();

        String userEmail =
                transaction.getUser().getEmail();

        String bookTitle =
                transaction.getBook().getTitle();

        String title =
                "Book Due Soon - Test";

        String message =
                "Test reminder: Your borrowed book \""
                        + bookTitle
                        + "\" is due on "
                        + transaction.getDueDate()
                        + ". Please return it on time to avoid a penalty.";

        System.out.println(
                "User ID: " + userId
        );

        System.out.println(
                "Book: " + bookTitle
        );

        System.out.println(
                "Email: " + userEmail
        );

        // =========================
        // IN-APP NOTIFICATION
        // =========================

        notificationService.createNotification(
                userId,
                title,
                message
        );

        System.out.println(
                "Test in-app notification created."
        );

        // =========================
        // EMAIL NOTIFICATION
        // =========================

        try {

            System.out.println(
                    "===== SENDING TEST EMAIL ====="
            );

            emailService.sendEmail(
                    userEmail,
                    title,
                    message
            );

            System.out.println(
                    "===== TEST EMAIL SENT SUCCESSFULLY ====="
            );

        } catch (Exception e) {

            System.out.println(
                    "===== TEST EMAIL FAILED ====="
            );

            System.out.println(
                    "Error: " + e.getMessage()
            );

            e.printStackTrace();
        }
    }
}
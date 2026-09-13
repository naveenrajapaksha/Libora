package com.libora.backend.repository;

import com.libora.backend.entity.User;
import com.libora.backend.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find latest non-deleted account by email
    Optional<User> findFirstByEmailAndStatusNotOrderByIdDesc(
            String email,
            UserStatus status
    );

    // Check whether a non-deleted account exists
    boolean existsByEmailAndStatusNot(
            String email,
            UserStatus status
    );

    // Find all accounts using the same email
    List<User> findAllByEmail(String email);

    /*
     * Compatibility method.
     *
     * Existing services such as TransactionService
     * can continue using findByEmail().
     */
    default Optional<User> findByEmail(String email) {

        return findAllByEmail(email)
                .stream()
                .filter(user ->
                        user.getStatus() != UserStatus.DELETED
                )
                .max((user1, user2) ->
                        user1.getId()
                                .compareTo(user2.getId())
                );
    }
}
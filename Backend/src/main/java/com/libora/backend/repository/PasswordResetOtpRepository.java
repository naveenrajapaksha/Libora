package com.libora.backend.repository;

import com.libora.backend.entity.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import java.util.Optional;

public interface PasswordResetOtpRepository
        extends JpaRepository<PasswordResetOtp, Long> {

    Optional<PasswordResetOtp> findTopByEmailOrderByIdDesc(
            String email
    );

    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetOtp p WHERE p.email = :email")
    void deleteByEmail(@Param("email") String email);
}
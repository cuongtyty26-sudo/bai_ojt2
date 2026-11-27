package com.example.baiojt2.repository;

import com.example.baiojt2.entity.PasswordResetOtp;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {
    @Modifying
    @Query("update PasswordResetOtp o set o.usedAt = :now where o.userId = :userId and o.usedAt is null")
    int invalidateActiveForUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PasswordResetOtp> findFirstByUserIdAndUsedAtIsNullOrderByCreatedAtDesc(Long userId);
}

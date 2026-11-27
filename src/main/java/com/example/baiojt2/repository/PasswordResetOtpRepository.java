package com.example.baiojt2.repository;

import com.example.baiojt2.entity.PasswordResetOtp;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {
    @Modifying
    @Query("update PasswordResetOtp o set o.usedAt = :now where o.userId = :userId and o.usedAt is null")
    int invalidateActiveForUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from PasswordResetOtp o where o.userId = :userId and o.usedAt is null order by o.createdAt desc")
    Optional<PasswordResetOtp> findActiveForUpdate(@Param("userId") Long userId);
}

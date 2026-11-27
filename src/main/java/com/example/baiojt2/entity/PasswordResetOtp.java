package com.example.baiojt2.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_otps")
public class PasswordResetOtp {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "otp_hash", nullable = false, length = 100)
    private String otpHash;
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    @Column(nullable = false)
    private int attempts;
    @Column(name = "used_at")
    private LocalDateTime usedAt;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected PasswordResetOtp() {}
    public PasswordResetOtp(Long userId, String otpHash, LocalDateTime expiresAt) {
        this.userId = userId; this.otpHash = otpHash; this.expiresAt = expiresAt;
        this.attempts = 0; this.createdAt = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getOtpHash() { return otpHash; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public int getAttempts() { return attempts; }
    public LocalDateTime getUsedAt() { return usedAt; }
    public void incrementAttempts() { this.attempts++; }
    public void invalidate() { this.usedAt = LocalDateTime.now(); }
    public boolean isExpired() { return !LocalDateTime.now().isBefore(expiresAt); }
}

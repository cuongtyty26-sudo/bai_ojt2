package com.example.baiojt2.service;

import com.example.baiojt2.entity.PasswordResetOtp;
import com.example.baiojt2.entity.UserAccount;
import com.example.baiojt2.repository.PasswordResetOtpRepository;
import com.example.baiojt2.repository.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class PasswordManagementService {
    private static final Logger log = LoggerFactory.getLogger(PasswordManagementService.class);
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int MAX_ATTEMPTS = 5;
    private final UserAccountRepository users;
    private final PasswordResetOtpRepository otps;
    private final PasswordEncoder encoder;

    public PasswordManagementService(UserAccountRepository users, PasswordResetOtpRepository otps,
                                     PasswordEncoder encoder) {
        this.users = users; this.otps = otps; this.encoder = encoder;
    }

    @Transactional
    public void changePassword(UserAccount user, String currentPassword, String newPassword) {
        UserAccount account = users.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản."));
        if (!encoder.matches(currentPassword, account.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không chính xác.");
        }
        account.changePasswordHash(encoder.encode(newPassword));
    }

    @Transactional
    public void requestPasswordReset(String email) {
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        users.findByEmailIgnoreCase(normalized).ifPresent(user -> {
            LocalDateTime now = LocalDateTime.now();
            otps.invalidateActiveForUser(user.getId(), now);
            String code = String.format(Locale.ROOT, "%06d", RANDOM.nextInt(1_000_000));
            otps.save(new PasswordResetOtp(user.getId(), encoder.encode(code), now.plusMinutes(10)));
            // Demo-only delivery replacement. Never log OTP values in production.
            log.warn("DEMO PASSWORD RESET OTP email={} code={} expiresAt={}", normalized, code, now.plusMinutes(10));
        });
    }

    @Transactional
    public void resetPassword(String email, String code, String newPassword) {
        UserAccount user = users.findByEmailIgnoreCase(email.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new IllegalArgumentException("Email hoặc OTP không hợp lệ."));
        PasswordResetOtp otp = otps.findActiveForUpdate(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("OTP không hợp lệ hoặc đã được sử dụng."));
        if (otp.isExpired()) {
            otp.invalidate();
            throw new IllegalArgumentException("OTP đã hết hạn. Vui lòng yêu cầu mã mới.");
        }
        if (otp.getAttempts() >= MAX_ATTEMPTS) {
            otp.invalidate();
            throw new IllegalArgumentException("OTP đã bị khóa do nhập sai quá 5 lần.");
        }
        if (!encoder.matches(code, otp.getOtpHash())) {
            otp.incrementAttempts();
            if (otp.getAttempts() >= MAX_ATTEMPTS) otp.invalidate();
            throw new IllegalArgumentException(otp.getAttempts() >= MAX_ATTEMPTS
                    ? "Bạn đã nhập sai OTP 5 lần. Mã đã bị hủy."
                    : "OTP không chính xác. Số lần thử còn lại: " + (MAX_ATTEMPTS - otp.getAttempts()) + ".");
        }
        user.changePasswordHash(encoder.encode(newPassword));
        otp.invalidate();
    }
}

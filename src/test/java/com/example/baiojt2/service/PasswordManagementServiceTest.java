package com.example.baiojt2.service;

import com.example.baiojt2.entity.PasswordResetOtp;
import com.example.baiojt2.entity.UserAccount;
import com.example.baiojt2.repository.PasswordResetOtpRepository;
import com.example.baiojt2.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PasswordManagementServiceTest {
    private UserAccountRepository users;
    private PasswordResetOtpRepository otps;
    private PasswordEncoder encoder;
    private PasswordManagementService service;
    private UserAccount user;

    @BeforeEach
    void setUp() {
        users = mock(UserAccountRepository.class);
        otps = mock(PasswordResetOtpRepository.class);
        encoder = new BCryptPasswordEncoder();
        service = new PasswordManagementService(users, otps, encoder);
        user = new UserAccount("student@example.test", encoder.encode("OldPass123"), "STUDENT", false, "ACTIVE");
        ReflectionTestUtils.setField(user, "id", 25L);
        when(users.findByEmailIgnoreCase("student@example.test")).thenReturn(Optional.of(user));
        when(users.findById(25L)).thenReturn(Optional.of(user));
    }

    @Test
    void changePasswordRequiresCorrectCurrentPassword() {
        assertThrows(IllegalArgumentException.class,
                () -> service.changePassword(user, "WrongPass123", "NewPass123"));
        assertTrue(encoder.matches("OldPass123", user.getPassword()));
        service.changePassword(user, "OldPass123", "NewPass123");
        assertTrue(encoder.matches("NewPass123", user.getPassword()));
    }

    @Test
    void fifthWrongOtpAttemptInvalidatesCodeAndPersistsAttemptCount() {
        PasswordResetOtp otp = new PasswordResetOtp(25L, encoder.encode("123456"), LocalDateTime.now().plusMinutes(10));
        for (int i = 0; i < 4; i++) otp.incrementAttempts();
        when(otps.findFirstByUserIdAndUsedAtIsNullOrderByCreatedAtDesc(25L)).thenReturn(Optional.of(otp));

        assertThrows(IllegalArgumentException.class,
                () -> service.resetPassword("student@example.test", "000000", "NewPass123"));

        assertEquals(5, otp.getAttempts());
        assertNotNull(otp.getUsedAt());
    }

    @Test
    void correctOtpChangesPasswordAndConsumesOtp() {
        PasswordResetOtp otp = new PasswordResetOtp(25L, encoder.encode("123456"), LocalDateTime.now().plusMinutes(10));
        when(otps.findFirstByUserIdAndUsedAtIsNullOrderByCreatedAtDesc(25L)).thenReturn(Optional.of(otp));

        service.resetPassword("student@example.test", "123456", "NewPass123");

        assertTrue(encoder.matches("NewPass123", user.getPassword()));
        assertNotNull(otp.getUsedAt());
    }

    @Test
    void expiredOtpIsInvalidated() {
        PasswordResetOtp otp = new PasswordResetOtp(25L, encoder.encode("123456"), LocalDateTime.now().minusSeconds(1));
        when(otps.findFirstByUserIdAndUsedAtIsNullOrderByCreatedAtDesc(25L)).thenReturn(Optional.of(otp));

        assertThrows(IllegalArgumentException.class,
                () -> service.resetPassword("student@example.test", "123456", "NewPass123"));
        assertNotNull(otp.getUsedAt());
    }
}

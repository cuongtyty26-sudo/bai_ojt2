package com.example.baiojt2.service;

import com.example.baiojt2.dto.RegistrationForm;
import com.example.baiojt2.entity.StudentProfile;
import com.example.baiojt2.entity.UserAccount;
import com.example.baiojt2.repository.StudentProfileRepository;
import com.example.baiojt2.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient();

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {
    @Mock private UserAccountRepository userRepository;
    @Mock private StudentProfileRepository profileRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private RegistrationService registrationService;

    private RegistrationForm validForm() {
        RegistrationForm form = new RegistrationForm();
        form.setFullName("  Nguyen Van A  ");
        form.setEmail("  Student@Example.Test ");
        form.setPassword("Password123");
        form.setConfirmPassword("Password123");
        return form;
    }

    @BeforeEach
    void setUp() {
        lenient().when(userRepository.existsByEmailIgnoreCase("student@example.test")).thenReturn(false);
        lenient().when(passwordEncoder.encode("Password123")).thenReturn("bcrypt-hash");
        lenient().when(userRepository.save(any(UserAccount.class))).thenAnswer(invocation -> {
            UserAccount account = invocation.getArgument(0);
            ReflectionTestUtils.setField(account, "id", 101L);
            return account;
        });
    }

    @Test
    void registersStudentWithNormalizedEmailAndTrimmedName() {
        registrationService.registerStudent(validForm());

        ArgumentCaptor<UserAccount> accountCaptor = ArgumentCaptor.forClass(UserAccount.class);
        verify(userRepository).save(accountCaptor.capture());
        assertEquals("student@example.test", accountCaptor.getValue().getEmail());
        assertEquals("STUDENT", accountCaptor.getValue().getRole());
        assertEquals("ACTIVE", accountCaptor.getValue().getAccountStatus());
        assertFalse(accountCaptor.getValue().isLocked());

        ArgumentCaptor<StudentProfile> profileCaptor = ArgumentCaptor.forClass(StudentProfile.class);
        verify(profileRepository).save(profileCaptor.capture());
        assertEquals(101L, profileCaptor.getValue().getUserId());
        assertEquals("Nguyen Van A", profileCaptor.getValue().getFullName());
    }

    @Test
    void encodesPasswordInsteadOfSavingPlainText() {
        registrationService.registerStudent(validForm());

        verify(passwordEncoder).encode("Password123");
        verify(userRepository).save(argThat(account ->
                "bcrypt-hash".equals(account.getPassword()) && !"Password123".equals(account.getPassword())));
    }

    @Test
    void rejectsDuplicateEmailWithoutCreatingAccountOrProfile() {
        when(userRepository.existsByEmailIgnoreCase("student@example.test")).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> registrationService.registerStudent(validForm()));

        verify(userRepository, never()).save(any());
        verify(profileRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void doesNotCreateProfileWhenAccountSaveFails() {
        when(userRepository.save(any(UserAccount.class))).thenThrow(new IllegalStateException("database failure"));

        assertThrows(IllegalStateException.class,
                () -> registrationService.registerStudent(validForm()));

        verify(profileRepository, never()).save(any());
    }
}

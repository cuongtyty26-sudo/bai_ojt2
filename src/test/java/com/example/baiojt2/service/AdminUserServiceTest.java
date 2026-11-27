package com.example.baiojt2.service;

import com.example.baiojt2.dto.CreatePartnerForm;
import com.example.baiojt2.entity.Company;
import com.example.baiojt2.entity.UserAccount;
import com.example.baiojt2.repository.CompanyRepository;
import com.example.baiojt2.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminUserServiceTest {
    private UserAccountRepository users;
    private CompanyRepository companies;
    private AdminUserService service;

    @BeforeEach
    void setUp() {
        users = mock(UserAccountRepository.class);
        companies = mock(CompanyRepository.class);
        service = new AdminUserService(users, companies, new BCryptPasswordEncoder());
    }

    @Test
    void adminCannotLockOwnAccount() {
        UserAccount admin = new UserAccount("admin@example.test", "hash", "ADMIN", false, "ACTIVE");
        ReflectionTestUtils.setField(admin, "id", 7L);
        when(users.findById(7L)).thenReturn(Optional.of(admin));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.setLocked(7L, true, 7L));

        assertTrue(error.getMessage().contains("không thể tự khóa"));
        assertFalse(admin.isLocked());
    }

    @Test
    void adminCanUnlockAnotherAccount() {
        UserAccount partner = new UserAccount("partner@example.test", "hash", "PARTNER", true, "ACTIVE");
        ReflectionTestUtils.setField(partner, "id", 9L);
        when(users.findById(9L)).thenReturn(Optional.of(partner));

        service.setLocked(9L, false, 7L);

        assertFalse(partner.isLocked());
    }

    @Test
    void creatingPartnerStoresCompanyAndEncodedPassword() {
        CreatePartnerForm form = new CreatePartnerForm();
        form.setCompanyName("Example Company");
        form.setEmail("PARTNER@example.test");
        form.setPassword("PartnerPass123");
        Company company = new Company("Example Company", "partner@example.test");
        ReflectionTestUtils.setField(company, "id", 44L);
        when(companies.save(any(Company.class))).thenReturn(company);
        when(users.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.createPartner(form);

        verify(companies).save(any(Company.class));
        verify(users).save(argThat(account -> account.getEmail().equals("partner@example.test")
                && account.getRole().equals("PARTNER")
                && account.getCompanyId().equals(44L)
                && !account.isLocked()
                && new BCryptPasswordEncoder().matches("PartnerPass123", account.getPassword())));
    }
}

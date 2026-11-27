package com.example.baiojt2.service;

import com.example.baiojt2.dto.RegistrationForm;
import com.example.baiojt2.entity.StudentProfile;
import com.example.baiojt2.entity.UserAccount;
import com.example.baiojt2.repository.StudentProfileRepository;
import com.example.baiojt2.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class RegistrationService {

    private final UserAccountRepository userAccountRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserAccountRepository userAccountRepository,
                               StudentProfileRepository studentProfileRepository,
                               PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerStudent(RegistrationForm form) {
        String normalizedEmail = form.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userAccountRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalStateException("Email này đã được sử dụng.");
        }

        UserAccount account = new UserAccount(
                normalizedEmail,
                passwordEncoder.encode(form.getPassword()),
                "STUDENT",
                false,
                "ACTIVE"
        );
        UserAccount savedAccount = userAccountRepository.save(account);

        StudentProfile profile = new StudentProfile(savedAccount.getId(), form.getFullName().trim());
        studentProfileRepository.save(profile);
    }
}

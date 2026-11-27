package com.example.baiojt2.validation;

import com.example.baiojt2.dto.RegistrationForm;
import com.example.baiojt2.repository.UserAccountRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class RegistrationValidator implements ConstraintValidator<ValidRegistration, RegistrationForm> {

    private final UserAccountRepository userAccountRepository;

    public RegistrationValidator(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public boolean isValid(RegistrationForm form, ConstraintValidatorContext context) {
        if (form == null) {
            return true;
        }

        boolean valid = true;
        context.disableDefaultConstraintViolation();

        if (form.getPassword() != null && !form.getPassword().equals(form.getConfirmPassword())) {
            context.buildConstraintViolationWithTemplate("Mật khẩu nhập lại không khớp.")
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
            valid = false;
        }

        String email = form.getEmail();
        if (email != null && !email.isBlank()
                && userAccountRepository.existsByEmailIgnoreCase(email.trim())) {
            context.buildConstraintViolationWithTemplate("Email này đã được sử dụng.")
                    .addPropertyNode("email")
                    .addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}

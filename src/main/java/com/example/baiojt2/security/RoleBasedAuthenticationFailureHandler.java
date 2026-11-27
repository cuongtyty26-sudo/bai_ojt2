package com.example.baiojt2.security;

import com.example.baiojt2.entity.UserAccount;
import com.example.baiojt2.repository.UserAccountRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class RoleBasedAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final UserAccountRepository userAccountRepository;

    public RoleBasedAuthenticationFailureHandler(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("email");
        Optional<UserAccount> account = email == null
                ? Optional.empty()
                : userAccountRepository.findByEmailIgnoreCase(email.trim());

        String reason;
        if (exception instanceof LockedException || account.map(UserAccount::isLocked).orElse(false)) {
            reason = "locked";
        } else if (exception instanceof DisabledException
                || account.map(user -> !"ACTIVE".equalsIgnoreCase(user.getAccountStatus())).orElse(false)) {
            String status = account.map(UserAccount::getAccountStatus).orElse("");
            reason = switch (status) {
                case "PENDING" -> "pending";
                case "REJECTED" -> "rejected";
                case "SUSPENDED" -> "suspended";
                default -> "disabled";
            };
        } else {
            // Do not reveal whether an active email exists when the password is wrong.
            reason = "invalid";
        }

        response.sendRedirect(request.getContextPath() + "/login?reason=" + reason);
    }
}

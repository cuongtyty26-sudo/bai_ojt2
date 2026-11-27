package com.example.baiojt2.controller;

import com.example.baiojt2.config.SecurityConfig;
import com.example.baiojt2.entity.UserAccount;
import com.example.baiojt2.repository.UserAccountRepository;
import com.example.baiojt2.security.RoleBasedAuthenticationFailureHandler;
import com.example.baiojt2.security.RoleBasedAuthenticationSuccessHandler;
import com.example.baiojt2.service.CustomUserDetailsService;
import com.example.baiojt2.service.EducationService;
import com.example.baiojt2.service.PasswordManagementService;
import com.example.baiojt2.service.RegistrationService;
import com.example.baiojt2.validation.RegistrationValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {
        LoginController.class,
        RegistrationController.class,
        PasswordManagementController.class,
        EducationController.class,
        RoleHomeController.class,
        ErrorPageController.class
})
@Import({SecurityConfig.class, RegistrationValidator.class})
@TestPropertySource(properties = "security.remember-me.key=test-remember-me-key-for-mockmvc")
class AuthenticationAndAuthorizationMvcTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private PasswordEncoder passwordEncoder;

    @MockBean private CustomUserDetailsService userDetailsService;
    @MockBean private RoleBasedAuthenticationSuccessHandler successHandler;
    @MockBean private RoleBasedAuthenticationFailureHandler failureHandler;
    @MockBean private RegistrationService registrationService;
    @MockBean private PasswordManagementService passwordManagementService;
    @MockBean private EducationService educationService;
    @MockBean private UserAccountRepository userAccountRepository;

    @Test
    void loginPageIsPublic() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void successfulLoginUsesEmailAndInvokesSuccessHandler() throws Exception {
        UserAccount student = new UserAccount("student@example.test",
                passwordEncoder.encode("Password123"), "STUDENT", false, "ACTIVE");
        when(userDetailsService.loadUserByUsername("student@example.test")).thenReturn(student);

        mockMvc.perform(post("/login").with(csrf())
                        .param("email", "student@example.test")
                        .param("password", "Password123"))
                .andExpect(status().isOk());

        verify(userDetailsService).loadUserByUsername("student@example.test");
        verify(successHandler).onAuthenticationSuccess(any(), any(), argThat(authentication ->
                authentication.getAuthorities().stream()
                        .anyMatch(authority -> "ROLE_STUDENT".equals(authority.getAuthority()))));
    }

    @Test
    void invalidLoginInvokesFailureHandler() throws Exception {
        UserAccount student = new UserAccount("student@example.test",
                passwordEncoder.encode("Password123"), "STUDENT", false, "ACTIVE");
        when(userDetailsService.loadUserByUsername("student@example.test")).thenReturn(student);

        mockMvc.perform(post("/login").with(csrf())
                        .param("email", "student@example.test")
                        .param("password", "WrongPassword"))
                .andExpect(status().isOk());

        verify(failureHandler).onAuthenticationFailure(any(), any(), any());
        verify(successHandler, never()).onAuthenticationSuccess(any(), any(), any());
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(roles = "ADMIN")
    void adminCanAccessAdminUrl() throws Exception {
        mockMvc.perform(get("/admin/"))
                .andExpect(status().isOk())
                .andExpect(view().name("role-home"));
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(roles = "STUDENT")
    void studentCannotAccessAdminUrl() throws Exception {
        mockMvc.perform(get("/admin/"))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/error/403"));
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(roles = "PARTNER")
    void partnerCannotAccessStudentUrl() throws Exception {
        mockMvc.perform(get("/me/"))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/error/403"));
    }

    @Test
    void unauthenticatedUserIsRedirectedFromAdminUrlToLogin() throws Exception {
        mockMvc.perform(get("/admin/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void registrationFormShowsValidationErrorsAndDoesNotCallService() throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                        .param("fullName", "")
                        .param("email", "not-an-email")
                        .param("password", "short")
                        .param("confirmPassword", "different"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "fullName", "email", "password"));

        verify(registrationService, never()).registerStudent(any());
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(roles = "STUDENT")
    void passwordChangeFormShowsValidationErrorsAndDoesNotCallService() throws Exception {
        mockMvc.perform(post("/account/password").with(csrf())
                        .param("currentPassword", "")
                        .param("newPassword", "short")
                        .param("confirmPassword", "different"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/change-password"))
                .andExpect(model().attributeHasFieldErrors("changePasswordForm", "currentPassword", "newPassword"));

        verify(passwordManagementService, never()).changePassword(any(), anyString(), anyString());
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(roles = "STUDENT")
    void educationFormRejectsMissingRequiredFields() throws Exception {
        mockMvc.perform(post("/me/educations").with(csrf())
                        .param("institution", "")
                        .param("major", "")
                        .param("startDate", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("me/education-form"))
                .andExpect(model().attributeHasFieldErrors("educationForm", "institution", "major", "startDate"));

        verify(educationService, never()).createEducation(anyLong(), any());
    }
}

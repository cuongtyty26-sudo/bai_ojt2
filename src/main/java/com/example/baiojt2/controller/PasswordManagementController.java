package com.example.baiojt2.controller;

import com.example.baiojt2.dto.ChangePasswordForm;
import com.example.baiojt2.dto.ForgotPasswordForm;
import com.example.baiojt2.dto.ResetPasswordForm;
import com.example.baiojt2.entity.UserAccount;
import com.example.baiojt2.service.PasswordManagementService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordManagementController {
    private final PasswordManagementService service;
    public PasswordManagementController(PasswordManagementService service) { this.service = service; }

    @GetMapping("/account/password")
    public String changePasswordPage(Model model) {
        if (!model.containsAttribute("changePasswordForm")) model.addAttribute("changePasswordForm", new ChangePasswordForm());
        return "account/change-password";
    }

    @PostMapping("/account/password")
    public String changePassword(@Valid @ModelAttribute("changePasswordForm") ChangePasswordForm form,
                                 BindingResult result, @AuthenticationPrincipal UserAccount user,
                                 RedirectAttributes redirect) {
        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "password.mismatch", "Mật khẩu xác nhận không khớp.");
        }
        if (result.hasErrors()) { form.clearPasswords(); return "account/change-password"; }
        try {
            service.changePassword(user, form.getCurrentPassword(), form.getNewPassword());
        } catch (IllegalArgumentException ex) {
            result.rejectValue("currentPassword", "password.invalid", ex.getMessage());
            form.clearPasswords();
            return "account/change-password";
        }
        redirect.addFlashAttribute("successMessage", "Đổi mật khẩu thành công.");
        return "redirect:/account/password";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model) {
        if (!model.containsAttribute("forgotPasswordForm")) model.addAttribute("forgotPasswordForm", new ForgotPasswordForm());
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String requestOtp(@Valid @ModelAttribute("forgotPasswordForm") ForgotPasswordForm form,
                             BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) return "forgot-password";
        service.requestPasswordReset(form.getEmail());
        redirect.addFlashAttribute("successMessage",
                "Nếu email tồn tại, mã OTP đã được tạo. Trong môi trường demo, hãy kiểm tra log ứng dụng.");
        redirect.addFlashAttribute("resetEmail", form.getEmail().trim());
        return "redirect:/reset-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@ModelAttribute("resetPasswordForm") ResetPasswordForm form,
                                    @RequestParam(required = false) String email, Model model) {
        if (form.getEmail() == null && email != null) form.setEmail(email);
        model.addAttribute("resetPasswordForm", form);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@Valid @ModelAttribute("resetPasswordForm") ResetPasswordForm form,
                                BindingResult result, RedirectAttributes redirect) {
        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "password.mismatch", "Mật khẩu xác nhận không khớp.");
        }
        if (result.hasErrors()) { form.clearPasswords(); return "reset-password"; }
        try {
            service.resetPassword(form.getEmail(), form.getOtp(), form.getNewPassword());
        } catch (IllegalArgumentException ex) {
            result.reject("reset.failed", ex.getMessage());
            form.clearPasswords();
            return "reset-password";
        }
        redirect.addFlashAttribute("successMessage", "Đặt lại mật khẩu thành công. Vui lòng đăng nhập.");
        return "redirect:/login";
    }
}

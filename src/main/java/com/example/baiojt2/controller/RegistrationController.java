package com.example.baiojt2.controller;

import com.example.baiojt2.dto.RegistrationForm;
import com.example.baiojt2.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("registrationForm")) {
            model.addAttribute("registrationForm", new RegistrationForm());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registrationForm") RegistrationForm form,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            // Keep name/email for correction, but never send a password back to the browser.
            form.clearPasswords();
            return "register";
        }

        try {
            registrationService.registerStudent(form);
        } catch (IllegalStateException exception) {
            bindingResult.rejectValue("email", "email.duplicate", exception.getMessage());
            form.clearPasswords();
            return "register";
        }

        redirectAttributes.addFlashAttribute("successMessage",
                "Đăng ký thành công! Hồ sơ của bạn đang ở trạng thái riêng tư. Hãy đăng nhập để tiếp tục.");
        return "redirect:/login?registered";
    }
}

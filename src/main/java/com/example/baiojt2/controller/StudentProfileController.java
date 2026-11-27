package com.example.baiojt2.controller;

import com.example.baiojt2.dto.StudentProfileForm;
import com.example.baiojt2.entity.UserAccount;
import com.example.baiojt2.service.StudentProfileService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StudentProfileController {

    private final StudentProfileService profileService;

    public StudentProfileController(StudentProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/me/profile")
    public String showMyProfile(@AuthenticationPrincipal UserAccount account, Model model) {
        model.addAttribute("profileForm", profileService.getMyProfile(account.getId()));
        return "me/profile";
    }

    @PostMapping("/me/profile")
    public String updateMyProfile(@AuthenticationPrincipal UserAccount account,
                                  @Valid @ModelAttribute("profileForm") StudentProfileForm form,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "me/profile";
        }

        profileService.updateMyProfile(account.getId(), form);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ thành công.");
        return "redirect:/me/profile";
    }
}

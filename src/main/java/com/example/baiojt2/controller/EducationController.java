package com.example.baiojt2.controller;

import com.example.baiojt2.dto.EducationForm;
import com.example.baiojt2.entity.UserAccount;
import com.example.baiojt2.service.EducationService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EducationController {

    private final EducationService educationService;

    public EducationController(EducationService educationService) {
        this.educationService = educationService;
    }

    @GetMapping("/me/educations/new")
    public String showCreateForm(Model model) {
        model.addAttribute("educationForm", new EducationForm());
        model.addAttribute("formTitle", "Thêm học vấn");
        model.addAttribute("formAction", "/me/educations");
        return "me/education-form";
    }

    @PostMapping("/me/educations")
    public String create(@AuthenticationPrincipal UserAccount account,
                         @Valid @ModelAttribute("educationForm") EducationForm form,
                         BindingResult bindingResult, Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formTitle", "Thêm học vấn");
            model.addAttribute("formAction", "/me/educations");
            return "me/education-form";
        }
        educationService.createEducation(account.getId(), form);
        redirectAttributes.addFlashAttribute("successMessage", "Đã thêm thông tin học vấn.");
        return "redirect:/me/profile";
    }

    @GetMapping("/me/educations/{educationId}/edit")
    public String showEditForm(@AuthenticationPrincipal UserAccount account,
                               @PathVariable Long educationId, Model model) {
        model.addAttribute("educationForm", educationService.getMyEducation(account.getId(), educationId));
        model.addAttribute("formTitle", "Chỉnh sửa học vấn");
        model.addAttribute("formAction", "/me/educations/" + educationId);
        return "me/education-form";
    }

    @PostMapping("/me/educations/{educationId}")
    public String update(@AuthenticationPrincipal UserAccount account,
                         @PathVariable Long educationId,
                         @Valid @ModelAttribute("educationForm") EducationForm form,
                         BindingResult bindingResult, Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formTitle", "Chỉnh sửa học vấn");
            model.addAttribute("formAction", "/me/educations/" + educationId);
            return "me/education-form";
        }
        educationService.updateEducation(account.getId(), educationId, form);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật thông tin học vấn.");
        return "redirect:/me/profile";
    }

    @PostMapping("/me/educations/{educationId}/delete")
    public String delete(@AuthenticationPrincipal UserAccount account,
                         @PathVariable Long educationId,
                         RedirectAttributes redirectAttributes) {
        educationService.deleteEducation(account.getId(), educationId);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa thông tin học vấn.");
        return "redirect:/me/profile";
    }
}

package com.example.baiojt2.controller;

import com.example.baiojt2.dto.StudentSkillForm;
import com.example.baiojt2.entity.UserAccount;
import com.example.baiojt2.service.StudentSkillService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StudentSkillController {

    private final StudentSkillService studentSkillService;

    public StudentSkillController(StudentSkillService studentSkillService) {
        this.studentSkillService = studentSkillService;
    }

    @PostMapping("/me/skills")
    public String addSkill(@AuthenticationPrincipal UserAccount account,
                           @Valid @ModelAttribute("studentSkillForm") StudentSkillForm form,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn kỹ năng và mức tự đánh giá từ 1 đến 5.");
            return "redirect:/me/profile";
        }
        try {
            studentSkillService.addSkill(account.getId(), form.getSkillId(), form.getProficiencyLevel());
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm kỹ năng vào hồ sơ.");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/me/profile";
    }

    @PostMapping("/me/skills/{skillId}")
    public String updateLevel(@AuthenticationPrincipal UserAccount account,
                              @PathVariable Long skillId,
                              @ModelAttribute("proficiencyLevel") Integer proficiencyLevel,
                              RedirectAttributes redirectAttributes) {
        try {
            studentSkillService.updateLevel(account.getId(), skillId, proficiencyLevel);
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật mức kỹ năng.");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/me/profile";
    }

    @PostMapping("/me/skills/{skillId}/remove")
    public String removeSkill(@AuthenticationPrincipal UserAccount account,
                              @PathVariable Long skillId,
                              RedirectAttributes redirectAttributes) {
        studentSkillService.removeSkill(account.getId(), skillId);
        redirectAttributes.addFlashAttribute("successMessage", "Đã bỏ kỹ năng khỏi hồ sơ.");
        return "redirect:/me/profile";
    }
}

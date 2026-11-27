package com.example.baiojt2.controller;

import com.example.baiojt2.dto.StudentProfileForm;
import com.example.baiojt2.dto.StudentSkillForm;
import com.example.baiojt2.dto.StudentSkillView;
import com.example.baiojt2.entity.SkillCategory;
import com.example.baiojt2.entity.SkillProficiencyLevel;
import com.example.baiojt2.entity.UserAccount;
import com.example.baiojt2.service.StudentProfileService;
import com.example.baiojt2.service.StudentSkillService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class StudentProfileController {

    private final StudentProfileService profileService;
    private final StudentSkillService studentSkillService;

    public StudentProfileController(StudentProfileService profileService,
                                    StudentSkillService studentSkillService) {
        this.profileService = profileService;
        this.studentSkillService = studentSkillService;
    }

    @GetMapping("/me/profile")
    public String showMyProfile(@AuthenticationPrincipal UserAccount account, Model model) {
        model.addAttribute("profileForm", profileService.getMyProfile(account.getId()));
        addSkillData(account, model);
        return "me/profile";
    }

    @PostMapping("/me/profile")
    public String updateMyProfile(@AuthenticationPrincipal UserAccount account,
                                  @Valid @ModelAttribute("profileForm") StudentProfileForm form,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addSkillData(account, model);
            return "me/profile";
        }

        profileService.updateMyProfile(account.getId(), form);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ thành công.");
        return "redirect:/me/profile";
    }

    private void addSkillData(UserAccount account, Model model) {
        List<StudentSkillView> skills = studentSkillService.findMySkills(account.getId());
        Map<SkillCategory, List<StudentSkillView>> grouped = skills.stream()
                .collect(Collectors.groupingBy(StudentSkillView::category,
                        () -> new EnumMap<>(SkillCategory.class), Collectors.toList()));

        for (SkillCategory category : SkillCategory.values()) {
            grouped.putIfAbsent(category, List.of());
        }

        model.addAttribute("skillCategories", SkillCategory.values());
        model.addAttribute("studentSkillGroups", grouped);
        model.addAttribute("availableSkills", studentSkillService.findAvailableSkills(account.getId()));
        model.addAttribute("skillProficiencyLevels", SkillProficiencyLevel.values());
        if (!model.containsAttribute("studentSkillForm")) {
            model.addAttribute("studentSkillForm", new StudentSkillForm());
        }
    }
}

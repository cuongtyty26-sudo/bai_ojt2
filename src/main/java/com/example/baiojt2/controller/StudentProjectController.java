package com.example.baiojt2.controller;

import com.example.baiojt2.dto.StudentProjectForm;
import com.example.baiojt2.entity.UserAccount;
import com.example.baiojt2.service.StudentProjectService;
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
public class StudentProjectController {

    private final StudentProjectService projectService;

    public StudentProjectController(StudentProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/me/projects/new")
    public String showCreateForm(@AuthenticationPrincipal UserAccount account, Model model,
                                 RedirectAttributes redirectAttributes) {
        if (projectService.remainingSlots(account.getId()) <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mỗi hồ sơ chỉ được có tối đa 20 dự án.");
            return "redirect:/me/profile";
        }
        model.addAttribute("projectForm", new StudentProjectForm());
        model.addAttribute("formTitle", "Thêm dự án");
        model.addAttribute("formAction", "/me/projects");
        return "me/project-form";
    }

    @PostMapping("/me/projects")
    public String create(@AuthenticationPrincipal UserAccount account,
                         @Valid @ModelAttribute("projectForm") StudentProjectForm form,
                         BindingResult bindingResult, Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formTitle", "Thêm dự án");
            model.addAttribute("formAction", "/me/projects");
            return "me/project-form";
        }
        try {
            projectService.create(account.getId(), form);
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm dự án vào hồ sơ.");
            return "redirect:/me/profile";
        } catch (IllegalStateException ex) {
            model.addAttribute("formTitle", "Thêm dự án");
            model.addAttribute("formAction", "/me/projects");
            model.addAttribute("projectLimitError", ex.getMessage());
            return "me/project-form";
        }
    }

    @GetMapping("/me/projects/{projectId}/edit")
    public String showEditForm(@AuthenticationPrincipal UserAccount account,
                               @PathVariable Long projectId, Model model) {
        model.addAttribute("projectForm", projectService.getProjectForm(account.getId(), projectId));
        model.addAttribute("formTitle", "Chỉnh sửa dự án");
        model.addAttribute("formAction", "/me/projects/" + projectId);
        return "me/project-form";
    }

    @PostMapping("/me/projects/{projectId}")
    public String update(@AuthenticationPrincipal UserAccount account,
                         @PathVariable Long projectId,
                         @Valid @ModelAttribute("projectForm") StudentProjectForm form,
                         BindingResult bindingResult, Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formTitle", "Chỉnh sửa dự án");
            model.addAttribute("formAction", "/me/projects/" + projectId);
            return "me/project-form";
        }
        projectService.update(account.getId(), projectId, form);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật dự án.");
        return "redirect:/me/profile";
    }

    @PostMapping("/me/projects/{projectId}/delete")
    public String delete(@AuthenticationPrincipal UserAccount account,
                         @PathVariable Long projectId,
                         RedirectAttributes redirectAttributes) {
        projectService.delete(account.getId(), projectId);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa dự án khỏi hồ sơ.");
        return "redirect:/me/profile";
    }
}

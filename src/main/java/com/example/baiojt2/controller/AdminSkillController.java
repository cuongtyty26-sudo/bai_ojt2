package com.example.baiojt2.controller;

import com.example.baiojt2.dto.SkillForm;
import com.example.baiojt2.entity.SkillCategory;
import com.example.baiojt2.service.AdminSkillService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminSkillController {

    private final AdminSkillService skillService;

    public AdminSkillController(AdminSkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping("/admin/skills")
    public String list(Model model) {
        model.addAttribute("skills", skillService.findAll());
        return "admin/skills";
    }

    @GetMapping("/admin/skills/new")
    public String showCreateForm(Model model) {
        model.addAttribute("skillForm", new SkillForm());
        model.addAttribute("formTitle", "Thêm kỹ năng");
        model.addAttribute("formAction", "/admin/skills");
        addCategories(model);
        return "admin/skill-form";
    }

    @PostMapping("/admin/skills")
    public String create(@Valid @ModelAttribute("skillForm") SkillForm form,
                         BindingResult bindingResult, Model model,
                         RedirectAttributes redirectAttributes) {
        validateUniqueName(form, null, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("formTitle", "Thêm kỹ năng");
            model.addAttribute("formAction", "/admin/skills");
            addCategories(model);
            return "admin/skill-form";
        }
        skillService.create(form);
        redirectAttributes.addFlashAttribute("successMessage", "Đã thêm kỹ năng.");
        return "redirect:/admin/skills";
    }

    @GetMapping("/admin/skills/{skillId}/edit")
    public String showEditForm(@PathVariable Long skillId, Model model) {
        model.addAttribute("skillForm", skillService.getForm(skillId));
        model.addAttribute("formTitle", "Chỉnh sửa kỹ năng");
        model.addAttribute("formAction", "/admin/skills/" + skillId);
        addCategories(model);
        return "admin/skill-form";
    }

    @PostMapping("/admin/skills/{skillId}")
    public String update(@PathVariable Long skillId,
                         @Valid @ModelAttribute("skillForm") SkillForm form,
                         BindingResult bindingResult, Model model,
                         RedirectAttributes redirectAttributes) {
        validateUniqueName(form, skillId, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("formTitle", "Chỉnh sửa kỹ năng");
            model.addAttribute("formAction", "/admin/skills/" + skillId);
            addCategories(model);
            return "admin/skill-form";
        }
        skillService.update(skillId, form);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật kỹ năng.");
        return "redirect:/admin/skills";
    }

    @PostMapping("/admin/skills/{skillId}/hide")
    public String hide(@PathVariable Long skillId, RedirectAttributes redirectAttributes) {
        skillService.hide(skillId);
        redirectAttributes.addFlashAttribute("successMessage", "Đã ẩn kỹ năng. Dữ liệu liên kết của sinh viên được giữ nguyên.");
        return "redirect:/admin/skills";
    }

    @PostMapping("/admin/skills/{skillId}/activate")
    public String activate(@PathVariable Long skillId, RedirectAttributes redirectAttributes) {
        skillService.activate(skillId);
        redirectAttributes.addFlashAttribute("successMessage", "Đã hiển thị lại kỹ năng.");
        return "redirect:/admin/skills";
    }

    @PostMapping("/admin/skills/{skillId}/delete")
    public String delete(@PathVariable Long skillId, RedirectAttributes redirectAttributes) {
        boolean deleted = skillService.deleteIfUnused(skillId);
        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa kỹ năng chưa được sinh viên sử dụng.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Kỹ năng đã được sinh viên sử dụng nên không thể xóa. Hãy ẩn kỹ năng này.");
        }
        return "redirect:/admin/skills";
    }

    private void validateUniqueName(SkillForm form, Long currentSkillId, BindingResult bindingResult) {
        if (form.getName() != null && !form.getName().isBlank()
                && skillService.isNameTaken(form.getName(), currentSkillId)) {
            bindingResult.rejectValue("name", "duplicate", "Tên kỹ năng đã tồn tại (không phân biệt chữ hoa/chữ thường).");
        }
    }

    private void addCategories(Model model) {
        model.addAttribute("categories", SkillCategory.values());
    }
}

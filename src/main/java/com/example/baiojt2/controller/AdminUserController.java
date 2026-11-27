package com.example.baiojt2.controller;

import com.example.baiojt2.dto.CreatePartnerForm;
import com.example.baiojt2.entity.UserAccount;
import com.example.baiojt2.service.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
    private final AdminUserService service;
    public AdminUserController(AdminUserService service) { this.service = service; }

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "ALL") String role,
                       @RequestParam(defaultValue = "ALL") String status,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size, Model model,\n                       @AuthenticationPrincipal UserAccount admin) {
        Page<?> users = service.search(keyword, role, status, page, size);
        model.addAttribute("usersPage", users);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedRole", role);
        model.addAttribute("selectedStatus", status);\n        model.addAttribute("currentAdminId", admin.getId());
        model.addAttribute("createPartnerForm", new CreatePartnerForm());
        return "admin/users";
    }

    @PostMapping("/partners")
    public String createPartner(@Valid @ModelAttribute("createPartnerForm") CreatePartnerForm form,
                                BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            redirect.addFlashAttribute("errorMessage", result.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/admin/users";
        }
        try {
            service.createPartner(form);
            redirect.addFlashAttribute("successMessage", "Đã tạo tài khoản đối tác thành công.");
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/lock")
    public String lock(@PathVariable Long id, @AuthenticationPrincipal UserAccount admin, RedirectAttributes redirect) {
        return changeLock(id, true, admin, redirect);
    }

    @PostMapping("/{id}/unlock")
    public String unlock(@PathVariable Long id, @AuthenticationPrincipal UserAccount admin, RedirectAttributes redirect) {
        return changeLock(id, false, admin, redirect);
    }

    private String changeLock(Long id, boolean locked, UserAccount admin, RedirectAttributes redirect) {
        try {
            service.setLocked(id, locked, admin.getId());
            redirect.addFlashAttribute("successMessage", locked ? "Đã khóa tài khoản." : "Đã mở khóa tài khoản.");
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/users";
    }
}

package com.example.baiojt2.controller;

import com.example.baiojt2.entity.UserAccount;
import com.example.baiojt2.service.StudentAvatarService;
import com.example.baiojt2.service.StudentAvatarService.AvatarImage;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StudentAvatarController {
    private final StudentAvatarService avatarService;

    public StudentAvatarController(StudentAvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(@AuthenticationPrincipal UserAccount account,
                         @RequestParam("avatarFile") MultipartFile avatarFile,
                         RedirectAttributes redirectAttributes) {
        try {
            avatarService.replaceMine(account.getId(), avatarFile);
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật ảnh đại diện.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/me/profile";
    }

    @GetMapping("/me/avatar")
    public ResponseEntity<FileSystemResource> image(@AuthenticationPrincipal UserAccount account) {
        AvatarImage avatar = avatarService.loadMine(account.getId());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(avatar.contentType()))
                .cacheControl(CacheControl.noStore())
                .header("X-Content-Type-Options", "nosniff")
                .body(new FileSystemResource(avatar.path()));
    }
}

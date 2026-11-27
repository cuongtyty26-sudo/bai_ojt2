package com.example.baiojt2.controller;

import com.example.baiojt2.dto.StudentCertificateForm;
import com.example.baiojt2.entity.UserAccount;
import com.example.baiojt2.service.StudentCertificateService;
import com.example.baiojt2.service.StudentCertificateService.ProtectedCertificateFile;
import jakarta.validation.Valid;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;

@Controller
public class StudentCertificateController {
    private final StudentCertificateService certificateService;

    public StudentCertificateController(StudentCertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping("/me/certificates/new")
    public String showForm(Model model) {
        model.addAttribute("certificateForm", new StudentCertificateForm());
        return "me/certificate-form";
    }

    @PostMapping(value = "/me/certificates", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String create(@AuthenticationPrincipal UserAccount account,
                         @Valid @ModelAttribute("certificateForm") StudentCertificateForm form,
                         BindingResult bindingResult, Model model,
                         RedirectAttributes redirectAttributes) {
        if (form.getEvidenceFile() == null || form.getEvidenceFile().isEmpty()) {
            bindingResult.rejectValue("evidenceFile", "required", "Vui lòng tải lên file minh chứng.");
        }
        if (bindingResult.hasErrors()) return "me/certificate-form";
        try {
            certificateService.create(account.getId(), form);
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm chứng chỉ vào hồ sơ.");
            return "redirect:/me/profile";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("uploadError", ex.getMessage());
            return "me/certificate-form";
        }
    }

    @PostMapping("/me/certificates/{certificateId}/delete")
    public String delete(@AuthenticationPrincipal UserAccount account,
                         @PathVariable Long certificateId,
                         RedirectAttributes redirectAttributes) {
        certificateService.deleteMine(account.getId(), certificateId);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa chứng chỉ và file minh chứng.");
        return "redirect:/me/profile";
    }

    @GetMapping("/me/certificates/{certificateId}/view")
    @ResponseBody
    public ResponseEntity<Resource> view(@AuthenticationPrincipal UserAccount account,
                                         @PathVariable Long certificateId) {
        return fileResponse(certificateService.loadMine(account.getId(), certificateId), false);
    }

    @GetMapping("/me/certificates/{certificateId}/download")
    @ResponseBody
    public ResponseEntity<Resource> download(@AuthenticationPrincipal UserAccount account,
                                             @PathVariable Long certificateId) {
        return fileResponse(certificateService.loadMine(account.getId(), certificateId), true);
    }

    private ResponseEntity<Resource> fileResponse(ProtectedCertificateFile file, boolean attachment) {
        MediaType mediaType = MediaType.parseMediaType(file.contentType());
        ContentDisposition disposition = (attachment
                ? ContentDisposition.attachment() : ContentDisposition.inline())
                .filename(file.originalName(), StandardCharsets.UTF_8).build();
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .header(HttpHeaders.X_CONTENT_TYPE_OPTIONS, "nosniff")
                .body(new FileSystemResource(file.path()));
    }
}

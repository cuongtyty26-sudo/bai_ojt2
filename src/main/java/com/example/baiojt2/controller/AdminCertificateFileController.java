package com.example.baiojt2.controller;

import com.example.baiojt2.service.StudentCertificateService;
import com.example.baiojt2.service.StudentCertificateService.ProtectedCertificateFile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/admin/certificates")
public class AdminCertificateFileController {
    private final StudentCertificateService certificateService;

    public AdminCertificateFileController(StudentCertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping("/{certificateId}/view")
    @ResponseBody
    public ResponseEntity<Resource> view(@PathVariable Long certificateId) {
        return fileResponse(certificateService.loadForAdmin(certificateId), false);
    }

    @GetMapping("/{certificateId}/download")
    @ResponseBody
    public ResponseEntity<Resource> download(@PathVariable Long certificateId) {
        return fileResponse(certificateService.loadForAdmin(certificateId), true);
    }

    private ResponseEntity<Resource> fileResponse(ProtectedCertificateFile file, boolean attachment) {
        ContentDisposition disposition = (attachment
                ? ContentDisposition.attachment() : ContentDisposition.inline())
                .filename(file.originalName(), StandardCharsets.UTF_8).build();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .header("X-Content-Type-Options", "nosniff")
                .body(new FileSystemResource(file.path()));
    }
}

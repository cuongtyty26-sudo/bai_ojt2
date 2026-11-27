package com.example.baiojt2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;

public class StudentCertificateForm {
    @NotBlank(message = "Vui lòng nhập tên chứng chỉ.")
    @Size(max = 200, message = "Tên chứng chỉ tối đa 200 ký tự.")
    private String name;

    @Size(max = 200, message = "Đơn vị cấp tối đa 200 ký tự.")
    private String issuer;

    @PastOrPresent(message = "Ngày cấp không được ở tương lai.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate issuedAt;

    private MultipartFile evidenceFile;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }
    public LocalDate getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDate issuedAt) { this.issuedAt = issuedAt; }
    public MultipartFile getEvidenceFile() { return evidenceFile; }
    public void setEvidenceFile(MultipartFile evidenceFile) { this.evidenceFile = evidenceFile; }
}

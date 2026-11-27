package com.example.baiojt2.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "certificates")
public class StudentCertificate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private StudentProfile profile;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 200)
    private String issuer;

    @Column(name = "issued_at")
    private LocalDate issuedAt;

    @Column(name = "original_file_name", length = 255)
    private String originalFileName;

    @Column(name = "stored_file_name", length = 100)
    private String storedFileName;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "file_size")
    private Long fileSize;

    protected StudentCertificate() {}

    public StudentCertificate(StudentProfile profile, String name, String issuer, LocalDate issuedAt,
                              String originalFileName, String storedFileName, String contentType, Long fileSize) {
        this.profile = profile;
        this.name = name;
        this.issuer = issuer;
        this.issuedAt = issuedAt;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
    }

    public Long getId() { return id; }
    public StudentProfile getProfile() { return profile; }
    public String getName() { return name; }
    public String getIssuer() { return issuer; }
    public LocalDate getIssuedAt() { return issuedAt; }
    public String getOriginalFileName() { return originalFileName; }
    public String getStoredFileName() { return storedFileName; }
    public String getContentType() { return contentType; }
    public Long getFileSize() { return fileSize; }
}

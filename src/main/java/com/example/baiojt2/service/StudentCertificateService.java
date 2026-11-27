package com.example.baiojt2.service;

import com.example.baiojt2.dto.StudentCertificateForm;
import com.example.baiojt2.entity.StudentCertificate;
import com.example.baiojt2.entity.StudentProfile;
import com.example.baiojt2.repository.StudentCertificateRepository;
import com.example.baiojt2.repository.StudentProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import java.util.List;

@Service
public class StudentCertificateService {
    private final StudentProfileRepository profileRepository;
    private final StudentCertificateRepository certificateRepository;
    private final CertificateFileStorageService storageService;

    public StudentCertificateService(StudentProfileRepository profileRepository,
                                     StudentCertificateRepository certificateRepository,
                                     CertificateFileStorageService storageService) {
        this.profileRepository = profileRepository;
        this.certificateRepository = certificateRepository;
        this.storageService = storageService;
    }

    @Transactional(readOnly = true)
    public List<StudentCertificate> findMine(Long userId) {
        StudentProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hồ sơ."));
        return certificateRepository.findAllByProfile_IdOrderByIssuedAtDescIdDesc(profile.getId());
    }

    @Transactional
    public void create(Long userId, StudentCertificateForm form) {
        StudentProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hồ sơ."));
        CertificateFileStorageService.StoredCertificateFile stored = storageService.store(form.getEvidenceFile());
        try {
            certificateRepository.saveAndFlush(new StudentCertificate(profile, form.getName().trim(),
                    blankToNull(form.getIssuer()), form.getIssuedAt(), stored.originalName(),
                    stored.storedName(), stored.contentType(), stored.size()));
        } catch (RuntimeException ex) {
            storageService.delete(stored.storedName());
            throw ex;
        }
    }

    @Transactional
    public void deleteMine(Long userId, Long certificateId) {
        StudentProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hồ sơ."));
        StudentCertificate certificate = certificateRepository.findByIdAndProfile_Id(certificateId, profile.getId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chứng chỉ trong hồ sơ của bạn."));
        certificateRepository.delete(certificate);
        storageService.delete(certificate.getStoredFileName());
    }

    @Transactional(readOnly = true)
    public ProtectedCertificateFile loadMine(Long userId, Long certificateId) {
        StudentProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hồ sơ."));
        StudentCertificate certificate = certificateRepository.findByIdAndProfile_Id(certificateId, profile.getId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chứng chỉ trong hồ sơ của bạn."));
        return loadFile(certificate);
    }

    @Transactional(readOnly = true)
    public ProtectedCertificateFile loadForAdmin(Long certificateId) {
        StudentCertificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chứng chỉ."));
        return loadFile(certificate);
    }

    private ProtectedCertificateFile loadFile(StudentCertificate certificate) {
        Path path = storageService.load(certificate.getStoredFileName());
        return new ProtectedCertificateFile(path, certificate.getOriginalFileName(),
                certificate.getContentType());
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public record ProtectedCertificateFile(Path path, String originalName, String contentType) {}
}

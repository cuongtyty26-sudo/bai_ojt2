package com.example.baiojt2.service;

import com.example.baiojt2.entity.StudentProfile;
import com.example.baiojt2.repository.StudentProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@Service
public class StudentAvatarService {
    private final StudentProfileRepository profileRepository;
    private final AvatarFileStorageService storageService;

    public StudentAvatarService(StudentProfileRepository profileRepository,
                                AvatarFileStorageService storageService) {
        this.profileRepository = profileRepository;
        this.storageService = storageService;
    }

    @Transactional
    public void replaceMine(Long authenticatedUserId, MultipartFile file) {
        storageService.validate(file);
        StudentProfile profile = findOwnedProfile(authenticatedUserId);
        String oldFileName = profile.getAvatarFileName();
        AvatarFileStorageService.StoredAvatar stored = storageService.store(file);
        profile.setAvatarFileName(stored.storedName());
        profile.setAvatarContentType(stored.contentType());

        if (oldFileName != null && !oldFileName.equals(stored.storedName())) {
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        storageService.delete(oldFileName);
                    }
                });
            } else {
                storageService.delete(oldFileName);
            }
        }
    }

    @Transactional(readOnly = true)
    public boolean hasAvatar(Long authenticatedUserId) {
        return findOwnedProfile(authenticatedUserId).getAvatarFileName() != null;
    }

    @Transactional(readOnly = true)
    public AvatarImage loadMine(Long authenticatedUserId) {
        StudentProfile profile = findOwnedProfile(authenticatedUserId);
        if (profile.getAvatarFileName() == null || profile.getAvatarContentType() == null) {
            throw new EntityNotFoundException("Tài khoản chưa có ảnh đại diện.");
        }
        return new AvatarImage(storageService.load(profile.getAvatarFileName()), profile.getAvatarContentType());
    }

    private StudentProfile findOwnedProfile(Long authenticatedUserId) {
        return profileRepository.findByUserId(authenticatedUserId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hồ sơ của tài khoản."));
    }

    public record AvatarImage(Path path, String contentType) {}
}

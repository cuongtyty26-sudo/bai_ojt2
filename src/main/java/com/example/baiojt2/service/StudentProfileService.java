package com.example.baiojt2.service;

import com.example.baiojt2.dto.StudentProfileForm;
import com.example.baiojt2.entity.StudentProfile;
import com.example.baiojt2.repository.StudentProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentProfileService {

    private final StudentProfileRepository profileRepository;

    public StudentProfileService(StudentProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Transactional(readOnly = true)
    public StudentProfileForm getMyProfile(Long authenticatedUserId) {
        return StudentProfileForm.from(findOwnedProfile(authenticatedUserId));
    }

    @Transactional
    public void updateMyProfile(Long authenticatedUserId, StudentProfileForm form) {
        StudentProfile profile = findOwnedProfile(authenticatedUserId);
        profile.setFullName(form.getFullName().trim());
        profile.setDateOfBirth(form.getDateOfBirth());
        profile.setPhone(blankToNull(form.getPhone()));
        profile.setSummary(blankToNull(form.getSummary()));
        profile.setDesiredPosition(blankToNull(form.getDesiredPosition()));
        profile.setDesiredWorkplace(blankToNull(form.getDesiredWorkplace()));
    }

    private StudentProfile findOwnedProfile(Long authenticatedUserId) {
        return profileRepository.findByUserId(authenticatedUserId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hồ sơ của tài khoản."));
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

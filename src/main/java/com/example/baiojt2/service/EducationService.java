package com.example.baiojt2.service;

import com.example.baiojt2.dto.EducationForm;
import com.example.baiojt2.entity.Education;
import com.example.baiojt2.entity.StudentProfile;
import com.example.baiojt2.repository.EducationRepository;
import com.example.baiojt2.repository.StudentProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EducationService {

    private final EducationRepository educationRepository;
    private final StudentProfileRepository profileRepository;

    public EducationService(EducationRepository educationRepository,
                            StudentProfileRepository profileRepository) {
        this.educationRepository = educationRepository;
        this.profileRepository = profileRepository;
    }

    @Transactional(readOnly = true)
    public List<Education> listMyEducations(Long authenticatedUserId) {
        StudentProfile profile = findOwnProfile(authenticatedUserId);
        return educationRepository.findAllByProfileIdOrderByStartDateDescIdDesc(profile.getId());
    }

    @Transactional(readOnly = true)
    public EducationForm getMyEducation(Long authenticatedUserId, Long educationId) {
        return EducationForm.from(findOwnedEducation(authenticatedUserId, educationId));
    }

    @Transactional
    public void createEducation(Long authenticatedUserId, EducationForm form) {
        StudentProfile profile = findOwnProfile(authenticatedUserId);
        Education education = new Education(profile.getId(), form.getInstitution().trim(),
                form.getMajor().trim(), form.getStartDate(), form.getEndDate(), form.getGpa());
        educationRepository.save(education);
    }

    @Transactional
    public void updateEducation(Long authenticatedUserId, Long educationId, EducationForm form) {
        Education education = findOwnedEducation(authenticatedUserId, educationId);
        education.setInstitution(form.getInstitution().trim());
        education.setMajor(form.getMajor().trim());
        education.setStartDate(form.getStartDate());
        education.setEndDate(form.getEndDate());
        education.setGpa(form.getGpa());
    }

    @Transactional
    public void deleteEducation(Long authenticatedUserId, Long educationId) {
        Education education = findOwnedEducation(authenticatedUserId, educationId);
        educationRepository.delete(education);
    }

    private StudentProfile findOwnProfile(Long authenticatedUserId) {
        return profileRepository.findByUserId(authenticatedUserId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hồ sơ của tài khoản."));
    }

    private Education findOwnedEducation(Long authenticatedUserId, Long educationId) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thông tin học vấn."));
        StudentProfile ownProfile = findOwnProfile(authenticatedUserId);
        if (!education.getProfileId().equals(ownProfile.getId())) {
            throw new AccessDeniedException("Bạn không có quyền chỉnh sửa thông tin học vấn này.");
        }
        return education;
    }
}

package com.example.baiojt2.service;

import com.example.baiojt2.dto.StudentProjectForm;
import com.example.baiojt2.entity.StudentProfile;
import com.example.baiojt2.entity.StudentProject;
import com.example.baiojt2.repository.StudentProfileRepository;
import com.example.baiojt2.repository.StudentProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentProjectService {

    public static final int MAX_PROJECTS_PER_PROFILE = 20;

    private final StudentProfileRepository profileRepository;
    private final StudentProjectRepository projectRepository;

    public StudentProjectService(StudentProfileRepository profileRepository,
                                 StudentProjectRepository projectRepository) {
        this.profileRepository = profileRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public List<StudentProject> findMyProjects(Long authenticatedUserId) {
        StudentProfile profile = findOwnedProfile(authenticatedUserId);
        return projectRepository.findAllByProfile_IdOrderByIdDesc(profile.getId());
    }

    @Transactional(readOnly = true)
    public StudentProjectForm getProjectForm(Long authenticatedUserId, Long projectId) {
        StudentProfile profile = findOwnedProfile(authenticatedUserId);
        StudentProject project = findOwnedProject(projectId, profile.getId());
        StudentProjectForm form = new StudentProjectForm();
        form.setName(project.getName());
        form.setRole(project.getRole());
        form.setTechnologies(project.getTechnologies());
        form.setGithubUrl(project.getGithubUrl());
        form.setDemoUrl(project.getDemoUrl());
        form.setDescription(project.getDescription());
        return form;
    }

    @Transactional(readOnly = true)
    public int remainingSlots(Long authenticatedUserId) {
        StudentProfile profile = findOwnedProfile(authenticatedUserId);
        return Math.max(0, MAX_PROJECTS_PER_PROFILE - (int) projectRepository.countByProfile_Id(profile.getId()));
    }

    @Transactional
    public void create(Long authenticatedUserId, StudentProjectForm form) {
        StudentProfile profile = profileRepository.findByUserIdForUpdate(authenticatedUserId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hồ sơ của tài khoản."));
        long currentCount = projectRepository.countByProfile_Id(profile.getId());
        if (currentCount >= MAX_PROJECTS_PER_PROFILE) {
            throw new IllegalStateException("Mỗi hồ sơ chỉ được có tối đa 20 dự án.");
        }
        projectRepository.save(new StudentProject(profile, normalize(form.getName()),
                normalize(form.getRole()), normalize(form.getTechnologies()),
                normalize(form.getGithubUrl()), normalizeOptional(form.getDemoUrl()),
                normalizeOptional(form.getDescription())));
    }

    @Transactional
    public void update(Long authenticatedUserId, Long projectId, StudentProjectForm form) {
        StudentProfile profile = findOwnedProfile(authenticatedUserId);
        StudentProject project = findOwnedProject(projectId, profile.getId());
        project.update(normalize(form.getName()), normalize(form.getRole()),
                normalize(form.getTechnologies()), normalize(form.getGithubUrl()),
                normalizeOptional(form.getDemoUrl()), normalizeOptional(form.getDescription()));
    }

    @Transactional
    public void delete(Long authenticatedUserId, Long projectId) {
        StudentProfile profile = findOwnedProfile(authenticatedUserId);
        StudentProject project = findOwnedProject(projectId, profile.getId());
        projectRepository.delete(project);
    }

    private StudentProfile findOwnedProfile(Long authenticatedUserId) {
        return profileRepository.findByUserId(authenticatedUserId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hồ sơ của tài khoản."));
    }

    private StudentProject findOwnedProject(Long projectId, Long profileId) {
        return projectRepository.findByIdAndProfile_Id(projectId, profileId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy dự án trong hồ sơ của bạn."));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeOptional(String value) {
        String normalized = normalize(value);
        return normalized.isEmpty() ? null : normalized;
    }
}

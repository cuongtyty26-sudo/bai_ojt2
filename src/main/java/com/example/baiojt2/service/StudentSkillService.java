package com.example.baiojt2.service;

import com.example.baiojt2.dto.SkillOption;
import com.example.baiojt2.dto.StudentSkillView;
import com.example.baiojt2.entity.ProfileSkill;
import com.example.baiojt2.entity.Skill;
import com.example.baiojt2.entity.SkillProficiencyLevel;
import com.example.baiojt2.entity.StudentProfile;
import com.example.baiojt2.repository.ProfileSkillRepository;
import com.example.baiojt2.repository.SkillRepository;
import com.example.baiojt2.repository.StudentProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentSkillService {

    private final StudentProfileRepository profileRepository;
    private final SkillRepository skillRepository;
    private final ProfileSkillRepository profileSkillRepository;

    public StudentSkillService(StudentProfileRepository profileRepository,
                               SkillRepository skillRepository,
                               ProfileSkillRepository profileSkillRepository) {
        this.profileRepository = profileRepository;
        this.skillRepository = skillRepository;
        this.profileSkillRepository = profileSkillRepository;
    }

    @Transactional(readOnly = true)
    public List<StudentSkillView> findMySkills(Long authenticatedUserId) {
        StudentProfile profile = findOwnedProfile(authenticatedUserId);
        return profileSkillRepository.findAllByProfile_IdOrderBySkill_CategoryAscSkill_NameAsc(profile.getId())
                .stream().map(StudentSkillView::from).toList();
    }

    @Transactional(readOnly = true)
    public List<SkillOption> findAvailableSkills(Long authenticatedUserId) {
        StudentProfile profile = findOwnedProfile(authenticatedUserId);
        Set<Long> selectedIds = profileSkillRepository
                .findAllByProfile_IdOrderBySkill_CategoryAscSkill_NameAsc(profile.getId())
                .stream().map(item -> item.getSkill().getId()).collect(Collectors.toSet());

        return skillRepository.findAllByActiveTrueOrderByCategoryAscNameAsc().stream()
                .filter(skill -> !selectedIds.contains(skill.getId()))
                .map(SkillOption::from)
                .toList();
    }

    @Transactional
    public void addSkill(Long authenticatedUserId, Long skillId, int proficiencyLevel) {
        SkillProficiencyLevel.fromValue(proficiencyLevel);
        StudentProfile profile = findOwnedProfile(authenticatedUserId);
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy kỹ năng."));
        if (!skill.isActive()) {
            throw new IllegalStateException("Kỹ năng này hiện không còn được phép chọn.");
        }
        if (profileSkillRepository.findByProfile_IdAndSkill_Id(profile.getId(), skillId).isPresent()) {
            throw new IllegalStateException("Bạn đã chọn kỹ năng này rồi.");
        }
        profileSkillRepository.save(new ProfileSkill(profile, skill, proficiencyLevel));
    }

    @Transactional
    public void updateLevel(Long authenticatedUserId, Long skillId, int proficiencyLevel) {
        SkillProficiencyLevel.fromValue(proficiencyLevel);
        StudentProfile profile = findOwnedProfile(authenticatedUserId);
        ProfileSkill profileSkill = profileSkillRepository
                .findByProfile_IdAndSkill_Id(profile.getId(), skillId)
                .orElseThrow(() -> new EntityNotFoundException("Kỹ năng không thuộc hồ sơ của bạn."));
        profileSkill.setProficiencyLevel(proficiencyLevel);
    }

    @Transactional
    public void removeSkill(Long authenticatedUserId, Long skillId) {
        StudentProfile profile = findOwnedProfile(authenticatedUserId);
        ProfileSkill profileSkill = profileSkillRepository
                .findByProfile_IdAndSkill_Id(profile.getId(), skillId)
                .orElseThrow(() -> new EntityNotFoundException("Kỹ năng không thuộc hồ sơ của bạn."));
        profileSkillRepository.delete(profileSkill);
    }

    private StudentProfile findOwnedProfile(Long authenticatedUserId) {
        return profileRepository.findByUserId(authenticatedUserId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hồ sơ của tài khoản."));
    }
}

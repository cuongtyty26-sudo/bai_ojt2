package com.example.baiojt2.service;

import com.example.baiojt2.dto.AdminSkillRow;
import com.example.baiojt2.dto.SkillForm;
import com.example.baiojt2.entity.Skill;
import com.example.baiojt2.repository.SkillRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminSkillService {

    private final SkillRepository skillRepository;

    public AdminSkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminSkillRow> findAll() {
        return skillRepository.findAllByOrderByCategoryAscNameAsc().stream()
                .map(skill -> AdminSkillRow.from(skill,
                        skillRepository.countStudentUsage(skill.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public SkillForm getForm(Long skillId) {
        return SkillForm.from(findSkill(skillId));
    }

    @Transactional(readOnly = true)
    public boolean isNameTaken(String name, Long currentSkillId) {
        String normalized = normalizeName(name);
        if (currentSkillId == null) {
            return skillRepository.existsByNameIgnoreCase(normalized);
        }
        return skillRepository.existsByNameIgnoreCaseAndIdNot(normalized, currentSkillId);
    }

    @Transactional
    public void create(SkillForm form) {
        skillRepository.save(new Skill(normalizeName(form.getName()), form.getCategory()));
    }

    @Transactional
    public void update(Long skillId, SkillForm form) {
        Skill skill = findSkill(skillId);
        skill.setName(normalizeName(form.getName()));
        skill.setCategory(form.getCategory());
    }

    @Transactional
    public void hide(Long skillId) {
        Skill skill = findSkill(skillId);
        skill.setActive(false);
    }

    @Transactional
    public void activate(Long skillId) {
        Skill skill = findSkill(skillId);
        skill.setActive(true);
    }

    @Transactional
    public boolean deleteIfUnused(Long skillId) {
        Skill skill = findSkill(skillId);
        if (skillRepository.countStudentUsage(skillId) > 0) {
            return false;
        }
        skillRepository.delete(skill);
        return true;
    }

    private Skill findSkill(Long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy kỹ năng."));
    }

    private String normalizeName(String name) {
        return name == null ? "" : name.trim().replaceAll("\\s+", " ");
    }
}

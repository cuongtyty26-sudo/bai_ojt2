package com.example.baiojt2.dto;

import com.example.baiojt2.entity.Skill;
import com.example.baiojt2.entity.SkillCategory;

public record SkillOption(Long id, String name, SkillCategory category, String categoryLabel) {
    public static SkillOption from(Skill skill) {
        return new SkillOption(skill.getId(), skill.getName(),
                skill.getCategory(), skill.getCategory().getLabel());
    }
}

package com.example.baiojt2.dto;

import com.example.baiojt2.entity.Skill;
import com.example.baiojt2.entity.SkillCategory;

public record AdminSkillRow(
        Long id,
        String name,
        SkillCategory category,
        boolean active,
        long usageCount
) {
    public static AdminSkillRow from(Skill skill, long usageCount) {
        return new AdminSkillRow(skill.getId(), skill.getName(),
                skill.getCategory(), skill.isActive(), usageCount);
    }
}

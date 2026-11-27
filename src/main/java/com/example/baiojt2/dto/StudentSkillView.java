package com.example.baiojt2.dto;

import com.example.baiojt2.entity.ProfileSkill;
import com.example.baiojt2.entity.SkillCategory;
import com.example.baiojt2.entity.SkillProficiencyLevel;

public record StudentSkillView(
        Long skillId,
        String skillName,
        SkillCategory category,
        int proficiencyLevel,
        String levelLabel,
        String levelDescription
) {
    public static StudentSkillView from(ProfileSkill profileSkill) {
        SkillProficiencyLevel level = SkillProficiencyLevel.fromValue(profileSkill.getProficiencyLevel());
        return new StudentSkillView(
                profileSkill.getSkill().getId(),
                profileSkill.getSkill().getName(),
                profileSkill.getSkill().getCategory(),
                level.getValue(),
                level.getLabel(),
                level.getDescription()
        );
    }
}

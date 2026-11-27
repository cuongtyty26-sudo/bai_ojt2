package com.example.baiojt2.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class StudentSkillForm {

    @NotNull(message = "Vui lòng chọn kỹ năng.")
    private Long skillId;

    @NotNull(message = "Vui lòng chọn mức tự đánh giá.")
    @Min(value = 1, message = "Mức kỹ năng phải từ 1 đến 5.")
    @Max(value = 5, message = "Mức kỹ năng phải từ 1 đến 5.")
    private Integer proficiencyLevel;

    public Long getSkillId() { return skillId; }
    public void setSkillId(Long skillId) { this.skillId = skillId; }
    public Integer getProficiencyLevel() { return proficiencyLevel; }
    public void setProficiencyLevel(Integer proficiencyLevel) { this.proficiencyLevel = proficiencyLevel; }
}

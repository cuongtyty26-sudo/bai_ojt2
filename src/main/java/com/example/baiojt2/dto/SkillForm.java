package com.example.baiojt2.dto;

import com.example.baiojt2.entity.Skill;
import com.example.baiojt2.entity.SkillCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SkillForm {

    @NotBlank(message = "Vui lòng nhập tên kỹ năng.")
    @Size(max = 100, message = "Tên kỹ năng không được vượt quá 100 ký tự.")
    private String name;

    @NotNull(message = "Vui lòng chọn nhóm kỹ năng.")
    private SkillCategory category;

    public static SkillForm from(Skill skill) {
        SkillForm form = new SkillForm();
        form.setName(skill.getName());
        form.setCategory(skill.getCategory());
        return form;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public SkillCategory getCategory() { return category; }
    public void setCategory(SkillCategory category) { this.category = category; }
}

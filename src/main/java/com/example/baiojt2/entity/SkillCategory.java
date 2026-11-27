package com.example.baiojt2.entity;

public enum SkillCategory {
    LANGUAGE("Ngôn ngữ"),
    FRAMEWORK("Framework"),
    DATABASE("CSDL"),
    TOOL("Công cụ"),
    SOFT_SKILL("Kỹ năng mềm");

    private final String label;

    SkillCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

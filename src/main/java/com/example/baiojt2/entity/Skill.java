package com.example.baiojt2.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SkillCategory category;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    protected Skill() {
    }

    public Skill(String name, SkillCategory category) {
        this.name = name;
        this.category = category;
        this.active = true;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public SkillCategory getCategory() { return category; }
    public void setCategory(SkillCategory category) { this.category = category; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}

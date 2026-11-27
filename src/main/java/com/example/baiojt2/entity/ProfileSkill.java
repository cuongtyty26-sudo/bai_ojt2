package com.example.baiojt2.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "profile_skills", uniqueConstraints =
        @UniqueConstraint(name = "uk_profile_skills_profile_skill", columnNames = {"profile_id", "skill_id"}))
public class ProfileSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private StudentProfile profile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "proficiency_level", nullable = false)
    private int proficiencyLevel;

    protected ProfileSkill() {
    }

    public ProfileSkill(StudentProfile profile, Skill skill, int proficiencyLevel) {
        this.profile = profile;
        this.skill = skill;
        setProficiencyLevel(proficiencyLevel);
    }

    public Long getId() { return id; }
    public StudentProfile getProfile() { return profile; }
    public Skill getSkill() { return skill; }
    public int getProficiencyLevel() { return proficiencyLevel; }

    public void setProficiencyLevel(int proficiencyLevel) {
        if (proficiencyLevel < 1 || proficiencyLevel > 5) {
            throw new IllegalArgumentException("Mức kỹ năng phải từ 1 đến 5.");
        }
        this.proficiencyLevel = proficiencyLevel;
    }
}

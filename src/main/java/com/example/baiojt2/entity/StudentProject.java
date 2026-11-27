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

@Entity
@Table(name = "projects")
public class StudentProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private StudentProfile profile;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 150)
    private String role;

    @Column(nullable = false, length = 500)
    private String technologies;

    @Column(name = "github_url", nullable = false, length = 500)
    private String githubUrl;

    @Column(name = "demo_url", length = 500)
    private String demoUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    protected StudentProject() {
    }

    public StudentProject(StudentProfile profile, String name, String role,
                          String technologies, String githubUrl, String demoUrl,
                          String description) {
        this.profile = profile;
        this.name = name;
        this.role = role;
        this.technologies = technologies;
        this.githubUrl = githubUrl;
        this.demoUrl = demoUrl;
        this.description = description;
    }

    public Long getId() { return id; }
    public StudentProfile getProfile() { return profile; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getTechnologies() { return technologies; }
    public String getGithubUrl() { return githubUrl; }
    public String getDemoUrl() { return demoUrl; }
    public String getDescription() { return description; }

    public void update(String name, String role, String technologies,
                       String githubUrl, String demoUrl, String description) {
        this.name = name;
        this.role = role;
        this.technologies = technologies;
        this.githubUrl = githubUrl;
        this.demoUrl = demoUrl;
        this.description = description;
    }
}

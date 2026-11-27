package com.example.baiojt2.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "educations")
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "institution", nullable = false, length = 200)
    private String institution;

    @Column(name = "major", length = 150)
    private String major;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "gpa", precision = 3, scale = 2)
    private BigDecimal gpa;

    protected Education() {
    }

    public Education(Long profileId, String institution, String major,
                     LocalDate startDate, LocalDate endDate, BigDecimal gpa) {
        this.profileId = profileId;
        this.institution = institution;
        this.major = major;
        this.startDate = startDate;
        this.endDate = endDate;
        this.gpa = gpa;
    }

    public Long getId() { return id; }
    public Long getProfileId() { return profileId; }
    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public BigDecimal getGpa() { return gpa; }
    public void setGpa(BigDecimal gpa) { this.gpa = gpa; }
}

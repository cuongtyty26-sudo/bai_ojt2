package com.example.baiojt2.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "profiles")
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "desired_position", length = 200)
    private String desiredPosition;

    @Column(name = "desired_workplace", length = 200)
    private String desiredWorkplace;

    @Column(name = "avatar_file_name", length = 64)
    private String avatarFileName;

    @Column(name = "avatar_content_type", length = 30)
    private String avatarContentType;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    protected StudentProfile() {
    }

    public StudentProfile(Long userId, String fullName) {
        this.userId = userId;
        this.fullName = fullName;
        this.isPublic = false;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getDesiredPosition() { return desiredPosition; }
    public void setDesiredPosition(String desiredPosition) { this.desiredPosition = desiredPosition; }
    public String getDesiredWorkplace() { return desiredWorkplace; }
    public void setDesiredWorkplace(String desiredWorkplace) { this.desiredWorkplace = desiredWorkplace; }
    public String getAvatarFileName() { return avatarFileName; }
    public void setAvatarFileName(String avatarFileName) { this.avatarFileName = avatarFileName; }
    public String getAvatarContentType() { return avatarContentType; }
    public void setAvatarContentType(String avatarContentType) { this.avatarContentType = avatarContentType; }
    public boolean isPublic() { return isPublic; }
}

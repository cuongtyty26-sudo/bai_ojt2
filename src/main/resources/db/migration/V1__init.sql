-- T4E7A5D: Create the complete recruitment platform schema.
-- MySQL 8.0+ / InnoDB / utf8mb4

CREATE TABLE companies (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    website VARCHAR(255),
    address VARCHAR(255),
    contact_email VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_companies_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    company_id BIGINT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    is_locked BOOLEAN NOT NULL DEFAULT FALSE,
    account_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email),
    KEY idx_users_company_id (company_id),
    KEY idx_users_role_status_locked (role, account_status, is_locked),
    CONSTRAINT fk_users_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE SET NULL,
    CONSTRAINT chk_users_role CHECK (role IN ('CANDIDATE', 'RECRUITER', 'ADMIN')),
    CONSTRAINT chk_users_account_status CHECK (account_status IN ('PENDING', 'ACTIVE', 'REJECTED', 'SUSPENDED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE profiles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    phone VARCHAR(30),
    location VARCHAR(150),
    headline VARCHAR(200),
    summary TEXT,
    is_public BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_profiles_user_id (user_id),
    KEY idx_profiles_public_location (is_public, location),
    CONSTRAINT fk_profiles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE educations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    profile_id BIGINT NOT NULL,
    institution VARCHAR(200) NOT NULL,
    degree VARCHAR(100),
    major VARCHAR(150),
    start_date DATE,
    end_date DATE,
    description TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_educations_profile_id (profile_id),
    CONSTRAINT fk_educations_profile FOREIGN KEY (profile_id) REFERENCES profiles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE skills (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_skills_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE profile_skills (
    id BIGINT NOT NULL AUTO_INCREMENT,
    profile_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    proficiency_level TINYINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_profile_skills_profile_skill (profile_id, skill_id),
    KEY idx_profile_skills_skill_level_profile (skill_id, proficiency_level, profile_id),
    CONSTRAINT fk_profile_skills_profile FOREIGN KEY (profile_id) REFERENCES profiles(id) ON DELETE CASCADE,
    CONSTRAINT fk_profile_skills_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE RESTRICT,
    CONSTRAINT chk_profile_skills_level CHECK (proficiency_level BETWEEN 1 AND 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE projects (
    id BIGINT NOT NULL AUTO_INCREMENT,
    profile_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    project_url VARCHAR(500),
    start_date DATE,
    end_date DATE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_projects_profile_id (profile_id),
    CONSTRAINT fk_projects_profile FOREIGN KEY (profile_id) REFERENCES profiles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE certificates (
    id BIGINT NOT NULL AUTO_INCREMENT,
    profile_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    issuer VARCHAR(200),
    credential_code VARCHAR(150),
    credential_url VARCHAR(500),
    issued_at DATE,
    expires_at DATE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_certificates_profile_id (profile_id),
    CONSTRAINT fk_certificates_profile FOREIGN KEY (profile_id) REFERENCES profiles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE shortlists (
    id BIGINT NOT NULL AUTO_INCREMENT,
    recruiter_user_id BIGINT NOT NULL,
    profile_id BIGINT NOT NULL,
    note VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_shortlists_recruiter_profile (recruiter_user_id, profile_id),
    KEY idx_shortlists_profile_id (profile_id),
    CONSTRAINT fk_shortlists_recruiter FOREIGN KEY (recruiter_user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_shortlists_profile FOREIGN KEY (profile_id) REFERENCES profiles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE interview_requests (
    id BIGINT NOT NULL AUTO_INCREMENT,
    recruiter_user_id BIGINT NOT NULL,
    profile_id BIGINT NOT NULL,
    job_title VARCHAR(200) NOT NULL,
    interview_type VARCHAR(20) NOT NULL,
    proposed_at DATETIME NOT NULL,
    location_or_meeting_url VARCHAR(500),
    message TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    responded_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_interviews_recruiter_status_created (recruiter_user_id, status, created_at),
    KEY idx_interviews_profile_status_proposed (profile_id, status, proposed_at),
    CONSTRAINT fk_interviews_recruiter FOREIGN KEY (recruiter_user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_interviews_profile FOREIGN KEY (profile_id) REFERENCES profiles(id) ON DELETE RESTRICT,
    CONSTRAINT chk_interviews_type CHECK (interview_type IN ('ONLINE', 'ONSITE')),
    CONSTRAINT chk_interviews_status CHECK (status IN ('PENDING', 'ACCEPTED', 'DECLINED', 'CANCELLED', 'EXPIRED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE profile_reviews (
    id BIGINT NOT NULL AUTO_INCREMENT,
    target_user_id BIGINT NOT NULL,
    reviewer_user_id BIGINT NOT NULL,
    profile_id BIGINT NULL,
    decision VARCHAR(20) NOT NULL,
    reason TEXT,
    reviewed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_profile_reviews_target_time (target_user_id, reviewed_at),
    KEY idx_profile_reviews_reviewer_time (reviewer_user_id, reviewed_at),
    CONSTRAINT fk_profile_reviews_target FOREIGN KEY (target_user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_profile_reviews_reviewer FOREIGN KEY (reviewer_user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_profile_reviews_profile FOREIGN KEY (profile_id) REFERENCES profiles(id) ON DELETE SET NULL,
    CONSTRAINT chk_profile_reviews_decision CHECK (decision IN ('APPROVED', 'REJECTED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE notifications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    recipient_user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT,
    target_url VARCHAR(500),
    read_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_notifications_recipient_read_created (recipient_user_id, read_at, created_at),
    CONSTRAINT fk_notifications_recipient FOREIGN KEY (recipient_user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE profile_views (
    id BIGINT NOT NULL AUTO_INCREMENT,
    viewer_user_id BIGINT NOT NULL,
    profile_id BIGINT NOT NULL,
    viewed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_profile_views_profile_time (profile_id, viewed_at),
    KEY idx_profile_views_viewer_time (viewer_user_id, viewed_at),
    CONSTRAINT fk_profile_views_viewer FOREIGN KEY (viewer_user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_profile_views_profile FOREIGN KEY (profile_id) REFERENCES profiles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

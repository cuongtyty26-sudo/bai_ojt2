-- T4E7A66: Add skill categories and soft visibility.
-- V7 follows the education migration V6 from task T4E7A65.
-- The existing skills.name unique key inherits utf8mb4_0900_ai_ci,
-- so uniqueness is case-insensitive on MySQL 8.
ALTER TABLE skills
    ADD COLUMN category VARCHAR(30) NOT NULL DEFAULT 'TOOL' AFTER name,
    ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE AFTER category;

ALTER TABLE skills
    ADD CONSTRAINT chk_skills_category
    CHECK (category IN ('LANGUAGE', 'FRAMEWORK', 'DATABASE', 'TOOL', 'SOFT_SKILL'));

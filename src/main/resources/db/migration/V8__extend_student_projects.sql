-- T4E7A68: extend projects for student portfolio management.
-- Existing demo projects predate GitHub links, so keep github_url nullable
-- for legacy rows. New form submissions require a valid HTTPS URL.
ALTER TABLE projects
    ADD COLUMN role VARCHAR(150) NOT NULL DEFAULT '' AFTER name,
    ADD COLUMN technologies VARCHAR(500) NOT NULL DEFAULT '' AFTER role,
    ADD COLUMN github_url VARCHAR(500) NULL AFTER technologies,
    ADD COLUMN demo_url VARCHAR(500) NULL AFTER github_url;

ALTER TABLE projects
    ADD CONSTRAINT chk_projects_github_url
    CHECK (github_url IS NULL OR github_url LIKE 'http://%' OR github_url LIKE 'https://');

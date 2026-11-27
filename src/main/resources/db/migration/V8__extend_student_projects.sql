-- T4E7A68: extend projects for student portfolio management.
ALTER TABLE projects
    ADD COLUMN role VARCHAR(150) NOT NULL DEFAULT '' AFTER name,
    ADD COLUMN technologies VARCHAR(500) NOT NULL DEFAULT '' AFTER role,
    ADD COLUMN github_url VARCHAR(500) NOT NULL DEFAULT '' AFTER technologies,
    ADD COLUMN demo_url VARCHAR(500) NULL AFTER github_url;

ALTER TABLE projects
    ADD CONSTRAINT chk_projects_github_url
    CHECK (github_url LIKE 'http://%' OR github_url LIKE 'https://');

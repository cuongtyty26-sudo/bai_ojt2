-- T4E7A60: Align persisted roles with the authentication task terminology.
-- Existing user accounts and all related foreign keys remain unchanged.
ALTER TABLE users DROP CHECK chk_users_role;

UPDATE users
SET role = CASE role
    WHEN 'CANDIDATE' THEN 'STUDENT'
    WHEN 'RECRUITER' THEN 'PARTNER'
    ELSE role
END;

ALTER TABLE users
    ADD CONSTRAINT chk_users_role CHECK (role IN ('STUDENT', 'PARTNER', 'ADMIN'));

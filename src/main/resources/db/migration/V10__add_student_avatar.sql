ALTER TABLE profiles
    ADD COLUMN avatar_file_name VARCHAR(64) NULL,
    ADD COLUMN avatar_content_type VARCHAR(30) NULL;

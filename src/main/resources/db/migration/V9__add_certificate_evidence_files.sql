-- T4E7A69: store private certificate evidence metadata.
ALTER TABLE certificates
    ADD COLUMN original_file_name VARCHAR(255) NULL AFTER issued_at,
    ADD COLUMN stored_file_name VARCHAR(100) NULL AFTER original_file_name,
    ADD COLUMN content_type VARCHAR(100) NULL AFTER stored_file_name,
    ADD COLUMN file_size BIGINT NULL AFTER content_type;

-- T4E7A65: Add optional GPA to education records.
ALTER TABLE educations
    ADD COLUMN gpa DECIMAL(3,2) NULL AFTER end_date;

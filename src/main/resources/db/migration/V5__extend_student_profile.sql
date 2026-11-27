-- T4E7A64: Extend student profile fields without modifying applied migrations.
ALTER TABLE profiles
    ADD COLUMN date_of_birth DATE NULL AFTER full_name,
    ADD COLUMN desired_position VARCHAR(200) NULL AFTER headline,
    ADD COLUMN desired_workplace VARCHAR(200) NULL AFTER desired_position;

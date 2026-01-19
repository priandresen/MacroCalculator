UPDATE user_profile
SET activity_level = 'MODERATE'
WHERE activity_level IS NULL;

ALTER TABLE user_profile
    ALTER COLUMN activity_level SET NOT NULL;

UPDATE user_profile
SET created_at = COALESCE(created_at, now()),
    updated_at = COALESCE(updated_at, now());

ALTER TABLE user_profile
    ALTER COLUMN created_at SET NOT NULL,
ALTER COLUMN updated_at SET NOT NULL;

ALTER TABLE user_profile
    ALTER COLUMN created_at SET DEFAULT now(),
ALTER COLUMN updated_at SET DEFAULT now();

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS trigger AS $$
BEGIN
  NEW.updated_at = now();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_user_profile_updated_at ON user_profile;

CREATE TRIGGER trg_user_profile_updated_at
    BEFORE UPDATE ON user_profile
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();
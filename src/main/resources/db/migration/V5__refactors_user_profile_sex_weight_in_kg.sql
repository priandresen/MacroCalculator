-- V5__add_sex_intensity_and_weight_kg.sql

ALTER TABLE user_profile
    ADD COLUMN IF NOT EXISTS sex VARCHAR(255),
    ADD COLUMN IF NOT EXISTS intensity VARCHAR(255);


UPDATE user_profile
SET sex = COALESCE(sex, 'FEMALE'),
    intensity = COALESCE(intensity, 'RECOMMENDED');

-- 3) Enforce NOT NULL after backfill
ALTER TABLE user_profile
    ALTER COLUMN sex SET NOT NULL,
    ALTER COLUMN intensity SET NOT NULL;

DO $$
    BEGIN
        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_name = 'user_profile'
              AND column_name = 'weight_grams'
        )
        THEN
            ALTER TABLE user_profile RENAME COLUMN weight_grams TO weight_kg;

            -- Convert existing data: grams -> kg
            UPDATE user_profile
            SET weight_kg = weight_kg / 1000.0;
        END IF;
    END $$;

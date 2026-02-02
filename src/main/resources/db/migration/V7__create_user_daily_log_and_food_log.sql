

-- one log per user per day
CREATE TABLE IF NOT EXISTS user_daily_log (
    id BIGSERIAL PRIMARY KEY,
    user_profile_id BIGINT NOT NULL,
    log_date DATE NOT NULL,

    CONSTRAINT fk_user_daily_log_user_profile
      FOREIGN KEY (user_profile_id)
          REFERENCES user_profile (id)
          ON DELETE CASCADE
);

-- Enforce "one daily log per user per day"
CREATE UNIQUE INDEX IF NOT EXISTS uq_user_daily_log_user_profile_date
    ON user_daily_log (user_profile_id, log_date);


-- food entries for that day
CREATE TABLE IF NOT EXISTS food_log (
    id BIGSERIAL PRIMARY KEY,
    user_daily_log_id BIGINT NOT NULL,

    usda_id BIGINT,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(255),

    serving_size DOUBLE PRECISION NOT NULL,
    serving_unit VARCHAR(50) NOT NULL,

    calories INTEGER NOT NULL,
    protein_g INTEGER NOT NULL,
    carbs_g INTEGER NOT NULL,
    fat_g INTEGER NOT NULL,

    CONSTRAINT fk_food_log_user_daily_log
        FOREIGN KEY (user_daily_log_id)
            REFERENCES user_daily_log (id)
            ON DELETE CASCADE
);

-- index for lookups
CREATE INDEX IF NOT EXISTS idx_food_log_user_daily_log_id
    ON food_log (user_daily_log_id);

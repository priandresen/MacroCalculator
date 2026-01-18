CREATE TABLE user_profile (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT,
  name VARCHAR(255) NOT NULL,
  weight_grams DOUBLE PRECISION NOT NULL,
  height_cm DOUBLE PRECISION NOT NULL,
  activity_level VARCHAR(255),
  body_fat_percentage DOUBLE PRECISION,
  created_at TIMESTAMP WITH TIME ZONE,
  updated_at TIMESTAMP WITH TIME ZONE
);


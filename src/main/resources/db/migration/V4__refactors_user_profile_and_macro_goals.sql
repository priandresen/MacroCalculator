CREATE UNIQUE INDEX macro_goal_one_active_per_user
    ON macro_goal (user_profile_id)
    WHERE is_active = true;

CREATE INDEX idx_macro_goal_user_profile_id
    ON macro_goal (user_profile_id);

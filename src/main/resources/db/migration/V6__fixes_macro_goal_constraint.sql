--Clean up any existing "bad" constraint/index if present.

ALTER TABLE macro_goal
    DROP CONSTRAINT IF EXISTS macro_goal_one_active_per_user;

DROP INDEX IF EXISTS macro_goal_one_active_per_user;
DROP INDEX IF EXISTS uq_macro_goal_one_active_per_user;

-- only one ACTIVE macro goal per user.

CREATE UNIQUE INDEX macro_goal_one_active_per_user
    ON macro_goal (user_profile_id)
    WHERE is_active = TRUE;

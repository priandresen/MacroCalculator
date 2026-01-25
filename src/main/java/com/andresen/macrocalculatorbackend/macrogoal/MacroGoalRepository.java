package com.andresen.macrocalculatorbackend.macrogoal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MacroGoalRepository extends JpaRepository<MacroGoal, Long> {

    Optional<MacroGoal> findByUserProfile_IdAndIsActiveTrue(Long userProfileId);

}

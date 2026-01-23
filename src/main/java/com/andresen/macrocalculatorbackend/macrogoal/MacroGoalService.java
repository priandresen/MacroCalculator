package com.andresen.macrocalculatorbackend.macrogoal;

import com.andresen.macrocalculatorbackend.exception.ResourceNotFoundException;
import com.andresen.macrocalculatorbackend.userprofile.UserProfile;
import com.andresen.macrocalculatorbackend.userprofile.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Service
public class MacroGoalService {

    private final UserProfileRepository userProfileRepository;
    private final MacroGoalRepository macroGoalRepository;

    public MacroGoalService(
            UserProfileRepository userProfileRepository,
            MacroGoalRepository macroGoalRepository
    ) {
        this.userProfileRepository = userProfileRepository;
        this.macroGoalRepository = macroGoalRepository;
    }

    public MacroGoal createMacroGoal(Long userProfileId, CreateMacroGoalDTO request) {
        UserProfile userProfile = userProfileRepository
                .findById(userProfileId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User profile not found")
                );

        MacroGoal macroGoal = new MacroGoal(
                userProfile,
                request.calorieTarget(),
                request.proteinG(),
                request.carbsG(),
                request.fatG(),
                request.isActive()
        );

        return macroGoalRepository.save(macroGoal);
    }

    /**
     * Automatic path: derive goal from the profile and ensure it's the ONLY active goal.
     */
    @Transactional
    public MacroGoal recalculateActiveGoalFromProfile(Long userProfileId) {
        UserProfile userProfile = userProfileRepository
                .findById(userProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found"));

        // 1) Deactivate the current active goal (if any)
        Optional<MacroGoal> existingActive =
                macroGoalRepository.findByUserProfileIdAndIsActiveTrue(userProfileId);

        existingActive.ifPresent(activeGoal -> {
            activeGoal.setActive(false);       // Assumption: MacroGoal has a setter like this.
            macroGoalRepository.save(activeGoal);
        });

        // 2) Compute the new goal from the profile
        // NOTE: I can't fill in the real formula without your calculation rules,
        // so I'm leaving a clear TODO.
        CalculatedMacros macros = calculateFromProfile(userProfile);

        // 3) Save a new active goal
        MacroGoal newActive = new MacroGoal(
                userProfile,
                macros.calories(),
                macros.proteinG(),
                macros.carbsG(),
                macros.fatG(),
                true
        );

        return macroGoalRepository.save(newActive);
    }

    private CalculatedMacros calculateFromProfile(UserProfile userProfile) {

        int age = Period.between(
                userProfile.getDateOfBirth(),
                LocalDate.now()
        ).getYears();



        // TODO: implement your macro calculation rules here (or delegate to a MacroCalculationService).
        // This method should use only fields from userProfile (weight, height, activityLevel, goal, etc.).
        throw new UnsupportedOperationException("Macro calculation not implemented yet");
    }

    private record CalculatedMacros(
            Integer calories,
            Integer proteinG,
            Integer carbsG,
            Integer fatG
    ) {}
}

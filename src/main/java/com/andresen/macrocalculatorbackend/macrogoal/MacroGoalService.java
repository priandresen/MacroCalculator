package com.andresen.macrocalculatorbackend.macrogoal;

import com.andresen.macrocalculatorbackend.exception.ResourceNotFoundException;
import com.andresen.macrocalculatorbackend.shared.ActivityLevel;
import com.andresen.macrocalculatorbackend.shared.Goal;
import com.andresen.macrocalculatorbackend.shared.Intensity;
import com.andresen.macrocalculatorbackend.shared.Sex;
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
            activeGoal.setActive(false);
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

    private int calculateBasalCalories(UserProfile userProfile) {
        Sex sex = userProfile.getSex();

        int age = Period.between(
                userProfile.getDateOfBirth(),
                LocalDate.now()
        ).getYears();

        Double bf = userProfile.getBodyFatPercentage();
        double basalCalories;

        if (sex == Sex.MALE) {
            if (bf != null && bf < 0.15) {
                basalCalories = (22 * (userProfile.getWeightKg() - (bf * userProfile.getWeightKg()))) + 500;
            } else {
                basalCalories =
                        (10 * userProfile.getWeightKg())
                                + (6.25 * userProfile.getHeightCm())
                                - (5 * age)
                                + 5;
            }
        } else { //FEMALE
            if (bf != null && bf < 0.2) {
                basalCalories = (22 * (userProfile.getWeightKg() - (bf * userProfile.getWeightKg()))) + 500;
            } else {
                basalCalories =
                        (10 * userProfile.getWeightKg())
                                + (6.25 * userProfile.getHeightCm())
                                - (5 * age)
                                - 161;
            }
        }

        return (int) Math.round(basalCalories);
    }

    private int calculateMaintenanceAfterLevelOfActivity(UserProfile userProfile){
        int bmr = calculateBasalCalories(userProfile);

        int adjustment = switch (userProfile.getActivityLevel()){
            case SEDENTARY -> 200;
            case MODERATE -> 400;
            case ACTIVE -> 600;
            case VERY_ACTIVE -> 1000;
        };

//        if (userProfile.getActivityLevel() == ActivityLevel.SEDENTARY){
//            targetCalories += 200;
//        } else if (userProfile.getActivityLevel() == ActivityLevel.ACTIVE){
//            targetCalories += 500;
//        } else {
//            targetCalories += 1000;
//        }
        return bmr + adjustment;
    }

    private int calculateTargetCaloriesLevelOfActivityToGoal(UserProfile userProfile) {
        int maintenance = calculateMaintenanceAfterLevelOfActivity(userProfile);

        return switch (userProfile.getGoal()) {
            case MAINTAIN -> maintenance;
            case LOSE_FAT -> maintenance - (int) Math.round(maintenance * 0.15);
            case GAIN_MUSCLE -> maintenance + (int) Math.round(maintenance * 0.20);
        };
    }


//        if (userProfile.getGoal() == Goal.GAIN_MUSCLE){
//            targetCalorie += (int) (targetCalorie*0.15);
//        } else if (userProfile.getGoal() == Goal.LOSE_FAT){
//            targetCalorie -= (int) (targetCalorie * 0.20);
//        } else {
//            return targetCalorie;
//        }
//        return targetCalorie;
//    }

    private int calculateTargetCaloriesToGoalAfterIntensity(UserProfile userProfile){
        int targetCalorie = calculateTargetCaloriesLevelOfActivityToGoal(userProfile);

        if (userProfile.getIntensity() == Intensity.INTENSE) {
            if (userProfile.getGoal() == Goal.LOSE_FAT){
                targetCalorie -= (int)(targetCalorie * 0.22);
            } else {
                targetCalorie += (int) (targetCalorie * 0.15);
            }
        }
        return  targetCalorie;
    }

    private CalculatedMacros calculateFromProfile(UserProfile userProfile) {

        int targetCalorie = calculateTargetCaloriesToGoalAfterIntensity(userProfile);

        int protein = (int) Math.round(userProfile.getWeightKg() * 1.6);
        int fat = (int) Math.round(userProfile.getWeightKg() * 1.0);

        int carbs = targetCalorie - ((protein * 4) + (fat * 9));
        carbs = carbs / 4;

        return new CalculatedMacros(
                targetCalorie,
                protein,
                carbs,
                fat
        );

    }

    private record CalculatedMacros(
            Integer calories,
            Integer proteinG,
            Integer carbsG,
            Integer fatG
    ) {}
}

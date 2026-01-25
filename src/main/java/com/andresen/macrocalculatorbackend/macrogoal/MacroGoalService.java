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
    private final MacroGoalDTOMapper macroGoalDTOMapper;

    public MacroGoalService(
            UserProfileRepository userProfileRepository,
            MacroGoalRepository macroGoalRepository,
            MacroGoalDTOMapper macroGoalDTOMapper
    ) {
        this.userProfileRepository = userProfileRepository;
        this.macroGoalRepository = macroGoalRepository;
        this.macroGoalDTOMapper = macroGoalDTOMapper;
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
    public MacroGoalDTO recalculateActiveGoalFromProfile(Long userProfileId) {
        UserProfile userProfile = userProfileRepository
                .findById(userProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found"));

        //Deactivate the current active goal
        Optional<MacroGoal> existingActive =
                macroGoalRepository.findByUserProfile_IdAndIsActiveTrue(userProfileId);

        existingActive.ifPresent(activeGoal -> {
            activeGoal.setActive(false);
            macroGoalRepository.save(activeGoal);
        });

        CalculatedMacros macros = calculateFromProfile(userProfile);

        //  Save a new active goal
        MacroGoal newActive = new MacroGoal(
                userProfile,
                macros.calories(),
                macros.proteinG(),
                macros.carbsG(),
                macros.fatG(),
                true
        );


        MacroGoal saved =  macroGoalRepository.save(newActive);
        return macroGoalDTOMapper.apply(saved);
    }

    public MacroGoalDTO getActiveMacroGoalByUserId(Long userProfileId) {

        if (!userProfileRepository.existsById(userProfileId)) {
            throw new ResourceNotFoundException("User profile not found");
        }

        MacroGoal activeGoal = macroGoalRepository
                .findByUserProfile_IdAndIsActiveTrue(userProfileId)
                .orElseThrow(() -> new IllegalStateException(
                        "No active MacroGoal for userProfileId=" + userProfileId
                ));

        return macroGoalDTOMapper.apply(activeGoal);
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

        int protein = (int) Math.round(userProfile.getWeightKg() * 2);
        int fat = (int) Math.round(userProfile.getWeightKg() * 0.9);

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

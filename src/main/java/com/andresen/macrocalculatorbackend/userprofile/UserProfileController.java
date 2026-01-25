package com.andresen.macrocalculatorbackend.userprofile;

import com.andresen.macrocalculatorbackend.macrogoal.MacroGoalDTO;
import com.andresen.macrocalculatorbackend.macrogoal.MacroGoalService;
import com.andresen.macrocalculatorbackend.onboarding.UserOnboardingResponse;
import com.andresen.macrocalculatorbackend.onboarding.UserOnboardingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user-profile")
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final UserOnboardingService userOnboardingService;
    private final MacroGoalService macroGoalService;

    public UserProfileController(UserProfileService userProfileService, UserOnboardingService userOnboardingService, MacroGoalService macroGoalService) {
        this.userProfileService = userProfileService;
        this.userOnboardingService = userOnboardingService;
        this.macroGoalService = macroGoalService;
    }

    @GetMapping
    public List<UserProfileDTO> getUserProfiles(){
        return userProfileService.getAllUserProfiles();
    }

    @GetMapping("/{id}")
    public UserProfileDTO getUserProfileById(
            @PathVariable Long id
    ){
        return userProfileService.getUserProfileById(id);
    }

    @GetMapping("/{id}/macros")
    public MacroGoalDTO getUserMacros(@PathVariable Long id){

        return macroGoalService.getActiveMacroGoalByUserId(id);
    }


//    @PostMapping
//    public ResponseEntity<UserProfileDTO> addNewUserProfile(@Valid @RequestBody CreateUserProfileDTO userProfile) {
//        UserProfileDTO createdUser = userProfileService.insertUserProfile(userProfile);
//        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
//    }

    @PostMapping
    public ResponseEntity<UserOnboardingResponse> createUserProfile(
            @Valid @RequestBody CreateUserProfileDTO request
    ) {
        UserOnboardingResponse created =  userOnboardingService.createProfileWithActiveMacroGoal(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }



}

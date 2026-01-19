package com.andresen.macrocalculatorbackend;

import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("user-profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
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


    @PostMapping
    public UserProfileDTO addNewUserProfile(@RequestBody CreateUserProfileDTO userProfile) {
        return userProfileService.insertUserProfile(userProfile);

    }

}

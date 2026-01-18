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
    public List<UserProfile> getUserProfiles(){
        return userProfileService.getAllUserProfiles();
    }

    @GetMapping("{id}")
    public UserProfile getUserProfileById(
            @PathVariable Integer id
    ){
        return userProfileService.getUserProfileById(id);
    }


    @PostMapping
    public void addNewUserProfile(@RequestBody UserProfile userProfile) {
        //USUALLY YOU DONT WANT TO USE THE ENTITY
        //what is the entity?
        userProfileService.insertUserProfile(userProfile);

    }

}

package com.andresen.macrocalculatorbackend.userprofile;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}/macros")
    public UserProfileDTO getUserMacros(@PathVariable Long id){
        return userProfileService.getUserProfileById(id);
    }


    @PostMapping
    public ResponseEntity<UserProfileDTO> addNewUserProfile(@Valid @RequestBody CreateUserProfileDTO userProfile) {
        UserProfileDTO createdUser = userProfileService.insertUserProfile(userProfile);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }



}

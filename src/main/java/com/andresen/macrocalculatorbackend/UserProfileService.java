package com.andresen.macrocalculatorbackend;

import org.springframework.stereotype.Service;

import java.util.List;

//this makes UserProfile available to use within other classes

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(
            UserProfileRepository userProfileRepository
    ) {
        this.userProfileRepository = userProfileRepository;
    }

    public List<UserProfile> getAllUserProfiles(){
        return userProfileRepository.findAll();
        //map into a DTO for the client
        //.stream().map()
        //minute 55 learn about DTO and
    }

    public void insertUserProfile(UserProfile userProfile) {
        //validade its not null
        userProfileRepository.save(userProfile);
    }

    public UserProfile getUserProfileById(Integer id) {
        return userProfileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        id + "not found"
                ));
    }
}

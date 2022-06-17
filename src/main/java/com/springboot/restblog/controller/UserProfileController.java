package com.springboot.restblog.controller;

import com.springboot.restblog.model.payload.UserProfileDTO;
import com.springboot.restblog.service.IUserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserProfileController {

    @Autowired
    private IUserProfileService userProfileService;

    @PutMapping("/profile/{userId}")
    public ResponseEntity<UserProfileDTO> updateProfile(@PathVariable(name = "userId") Integer userId,
                                                        @RequestBody UserProfileDTO profileDTO) {
        UserProfileDTO profileResponse = userProfileService.setInfoUser(userId, profileDTO);

        return new ResponseEntity<>(profileResponse, HttpStatus.OK);  //error
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable(name = "userId") Integer userId) {
        UserProfileDTO profileResponse = userProfileService.getProfileByUser(userId);

        return new ResponseEntity<>(profileResponse, HttpStatus.OK);  //error
    }
}

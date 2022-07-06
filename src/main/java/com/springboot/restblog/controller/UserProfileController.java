package com.springboot.restblog.controller;

import com.springboot.restblog.anotation.ValidImage;
import com.springboot.restblog.model.payload.CustomUser;
import com.springboot.restblog.model.payload.UserProfileDTO;
import com.springboot.restblog.service.IUserProfileService;
import com.springboot.restblog.utils.FileUploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
@Validated
public class UserProfileController {

    @Autowired
    private IUserProfileService userProfileService;

    @PutMapping("/profile")
    public ResponseEntity<UserProfileDTO> updateProfile(@RequestBody UserProfileDTO profileDTO) {
        UserProfileDTO profileResponse = userProfileService.setInfoUser(profileDTO);

        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable(name = "userId") Integer userId) {
        UserProfileDTO profileResponse = userProfileService.getProfileByUser(userId);

        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }

    @PostMapping("/profile/avatar")
    public ResponseEntity<String> updateAvatar(
            @RequestPart ("image") @Valid @ValidImage MultipartFile multipartFile) throws IOException {

        String urlAvartarPhoto = userProfileService.setAvatarImg(multipartFile);
        return new ResponseEntity<>(urlAvartarPhoto, HttpStatus.OK);
    }

    @PostMapping("/profile/coverPhoto")
    public ResponseEntity<String> updateCoverPhoto(
            @RequestPart ("image") @Valid @ValidImage MultipartFile multipartFile) throws IOException {

        String urlCoverPhoto = userProfileService.setCoverImg(multipartFile);
        return new ResponseEntity<>(urlCoverPhoto, HttpStatus.OK);
    }
}

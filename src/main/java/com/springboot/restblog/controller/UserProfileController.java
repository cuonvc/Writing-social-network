package com.springboot.restblog.controller;

import com.springboot.restblog.anotation.ValidImage;
import com.springboot.restblog.model.payload.PageResponseProfile;
import com.springboot.restblog.model.payload.UserProfileDTO;
import com.springboot.restblog.service.IUserProfileService;
import com.springboot.restblog.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
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

    @GetMapping("/profile/{username}")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable(name = "username") String username) {
        UserProfileDTO profileResponse = userProfileService.getProfileByUsername(username);

        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/profiles/role/{role_id}")
    public PageResponseProfile getAllProfileByRole(@PathVariable Integer role_id,
                                                   @RequestParam(value = "pageNo", required = false,
                                                            defaultValue = AppConstants.PAGE_NUMBER) Integer pageNo,
                                                   @RequestParam(value = "pageSize", required = false,
                                                            defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
                                                   @RequestParam(value = "sortBy", required = false,
                                                            defaultValue = AppConstants.SORT_BY) String sortBy,
                                                   @RequestParam(value = "sortDir", required = false,
                                                            defaultValue = AppConstants.SORT_DIRECTION) String dir) {
        return userProfileService.getAllByRole(role_id, pageNo, pageSize, sortBy, dir);
    }

    @GetMapping("/profiles/search")
    public PageResponseProfile searchProfileByKeyword(@RequestParam("keyword") String keyword,
                                                      @RequestParam(value = "pageNo", required = false,
                                                              defaultValue = AppConstants.PAGE_NUMBER) Integer pageNo,
                                                      @RequestParam(value = "pageSize", required = false,
                                                              defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
                                                      @RequestParam(value = "sortBy", required = false,
                                                              defaultValue = AppConstants.SORT_BY) String sortBy,
                                                      @RequestParam(value = "sortDir", required = false,
                                                              defaultValue = AppConstants.SORT_DIRECTION) String dir) {
        PageResponseProfile profileList = userProfileService.filterByKeyword(keyword, pageNo, pageSize, sortBy, dir);
        return profileList;
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

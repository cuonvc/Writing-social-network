package com.springboot.restblog.service;

import com.springboot.restblog.model.payload.UserProfileDTO;

public interface IUserProfileService {
    UserProfileDTO getProfileByUser(Integer userId);
    UserProfileDTO setInfoUser(Integer userId, UserProfileDTO userProfileDTO);
}

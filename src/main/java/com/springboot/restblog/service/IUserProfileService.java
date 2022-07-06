package com.springboot.restblog.service;

import com.springboot.restblog.model.payload.UserProfileDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IUserProfileService {
    UserProfileDTO getProfileByUser(Integer userId);
    UserProfileDTO setInfoUser(UserProfileDTO userProfileDTO);
    String setAvatarImg(MultipartFile multipartFile) throws IOException;
    String setCoverImg(MultipartFile multipartFile) throws IOException;
}

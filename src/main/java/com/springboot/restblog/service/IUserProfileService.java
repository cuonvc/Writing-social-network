package com.springboot.restblog.service;

import com.springboot.restblog.model.payload.PageResponseProfile;
import com.springboot.restblog.model.payload.UserProfileDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IUserProfileService {
    UserProfileDTO getProfileByUser(Integer userId);
    PageResponseProfile getAllByRole(Integer role_id, Integer pageNo, Integer pageSize, String sortBy, String dir);
    List<UserProfileDTO> filterByKeyword(String keyword);
    UserProfileDTO setInfoUser(UserProfileDTO userProfileDTO);
    String setAvatarImg(MultipartFile multipartFile) throws IOException;
    String setCoverImg(MultipartFile multipartFile) throws IOException;
}

package com.springboot.restblog.service.impl;

import com.springboot.restblog.exception.APIException;
import com.springboot.restblog.exception.ResourceNotFoundException;
import com.springboot.restblog.model.converter.UserProfileConverter;
import com.springboot.restblog.model.entity.UserEntity;
import com.springboot.restblog.model.entity.UserProfileEntity;
import com.springboot.restblog.model.payload.CustomUser;
import com.springboot.restblog.model.payload.UserProfileDTO;
import com.springboot.restblog.repository.UserProfileRepository;
import com.springboot.restblog.repository.UserRepository;
import com.springboot.restblog.service.IUserProfileService;
import com.springboot.restblog.utils.FileUploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Path;

@Service
public class UserProfileServiceImpl implements IUserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileConverter converter;

    @Override
    public UserProfileDTO getProfileByUser(Integer userId) {

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        UserProfileEntity userProfile = userProfileRepository.findUserProfileEntityByUser(userEntity).get();

        UserProfileDTO profileResponse = converter.toDto(userProfile);
        setUrlAvartarAndCover(userProfile, profileResponse);

        return profileResponse;
    }

    @Override
    public UserProfileDTO setInfoUser(UserProfileDTO userProfileDTO) {  //temporary logic

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Integer id = customUser.getUserId();

        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        UserProfileEntity oldProfile = userProfileRepository.findUserProfileEntityByUser(userEntity).get();

        UserProfileEntity newProfile = userProfileRepository.save(converter.toEntity(oldProfile, userProfileDTO));
        UserProfileDTO responseDto = converter.toDto(newProfile);
        setUrlAvartarAndCover(newProfile, responseDto);

        return responseDto;
    }

    @Override
    public String setAvatarImg(MultipartFile file) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Integer userId = customUser.getUserId();

        UserProfileEntity profileEntity = userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!file.isEmpty()) {
            String uploadDir = "uploaded-images/user-avatars/" + profileEntity.getId();
            Path path = saveToFolder(file, uploadDir);  //dau bi nguoc

            //replace "\" to "/"
            profileEntity.setAvatarPhoto(path.toString().replace("\\", "/"));  //convert lại cho xuôi
        } else {
            if (profileEntity.getAvatarPhoto().isEmpty()) {
                profileEntity.setAvatarPhoto(null);
            }
        }

        UserProfileEntity newProfile = userProfileRepository.save(profileEntity);

        String urlAvartar = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("files/" + newProfile.getAvatarPhoto())
                .toUriString();
        return urlAvartar;
    }

    @Override
    public String setCoverImg(MultipartFile file) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Integer userId = customUser.getUserId();

        UserProfileEntity profileEntity = userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!file.isEmpty()) {
            String uploadDir = "uploaded-images/user-covers/" + userId;
            Path path = saveToFolder(file, uploadDir);  //dau bi nguoc

            //replace "\" to "/"
            profileEntity.setCoverPhoto(path.toString().replace("\\", "/"));  //convert lại cho xuôi
        } else {
            if (profileEntity.getCoverPhoto().isEmpty()) {
                profileEntity.setCoverPhoto(null);
            }
        }

        UserProfileEntity newProfile = userProfileRepository.save(profileEntity);

        String urlCover = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("files/" + newProfile.getCoverPhoto())
                .toUriString();
        return urlCover;
    }

    private Path saveToFolder(MultipartFile multipartFile, String dir) throws IOException {
        float fileSizeMegabytes = multipartFile.getSize() / 1000000.0f;
        if (fileSizeMegabytes > 5.0f) {
            throw new RuntimeException("File must be maximum 5 megabytes");
        }

        FileUploadUtils.cleanDir(dir);
        Path filePath = FileUploadUtils.saveFile(dir, multipartFile);

        return filePath;
    }

    private void setUrlAvartarAndCover(UserProfileEntity savedProfile, UserProfileDTO response) {
        String urlAvartar = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("files/" + savedProfile.getAvatarPhoto())
                .toUriString();
        String urlCover = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("files/" + savedProfile.getCoverPhoto())
                .toUriString();
        response.setAvatarPhoto(urlAvartar);
        response.setCoverPhoto(urlCover);
    }
}

package com.springboot.restblog.service.impl;

import com.springboot.restblog.exception.APIException;
import com.springboot.restblog.exception.ResourceNotFoundException;
import com.springboot.restblog.model.converter.UserProfileConverter;
import com.springboot.restblog.model.entity.PostEntity;
import com.springboot.restblog.model.entity.RoleEntity;
import com.springboot.restblog.model.entity.UserEntity;
import com.springboot.restblog.model.entity.UserProfileEntity;
import com.springboot.restblog.model.payload.*;
import com.springboot.restblog.repository.RoleRepository;
import com.springboot.restblog.repository.UserProfileRepository;
import com.springboot.restblog.repository.UserRepository;
import com.springboot.restblog.service.IUserProfileService;
import com.springboot.restblog.utils.FileUploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserProfileServiceImpl implements IUserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserProfileConverter converter;

    @Override
    public UserProfileDTO getProfileByUsername(String username) {

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", username));

        UserProfileEntity userProfile = userProfileRepository.findUserProfileEntityByUser(userEntity).get();

        UserProfileDTO profileResponse = converter.toDto(userProfile);
        profileResponse.setEmailByUser(userEntity.getEmail());
        setUrlAvartarAndCover(userProfile, profileResponse);

        return profileResponse;
    }

    @Override
    public PageResponseProfile getAllByRole(Integer role_id, Integer pageNo,
                                     Integer pageSize, String sortBy, String sortDir) {
        RoleEntity roleEntity = roleRepository.findById(role_id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", role_id));
        List<UserEntity> userEntityList = userRepository.findUserEntitiesByRoles(roleEntity);

        List<UserProfileEntity> profileEntityList
                = userEntityList.stream()
                .map(UserEntity::getUserProfile).collect(Collectors.toList());

        Sort sortOj = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sortOj);

        Page<UserProfileEntity> page =
                new PageImpl<>(profileEntityList, pageable, profileEntityList.size());
        PageResponseProfile pageResponse = pagingProfile(page);

        return pageResponse;
    }

    @Override
    public PageResponseProfile filterByKeyword(String keyword, Integer pageNo, Integer pageSize,
                                               String sortBy, String sortDir) {
        String newStr = keyword.trim()
                .replaceAll("[ ]+", " "); //multiple spaces to single space
        String[] firstOrLastName = newStr.split(" ");
        List<UserProfileEntity> listResponseEntity = new ArrayList<>();

        for (String key : firstOrLastName) {
            List<UserProfileEntity> profileList = userProfileRepository.searchProfiles(key);

            for (UserProfileEntity profileEntity : profileList) {
                if (listResponseEntity.isEmpty()) {
                    listResponseEntity.add(profileEntity);
                } else {
                    //error (ConcurrentModificationException) when using forEach
                    int count = 0;
                    for (int i = 0; i < listResponseEntity.size(); i++) {
                        if (!profileEntity.getId().equals(listResponseEntity.get(i).getId())) {
                            count++;
                        }
                    }
                    for (int i = 0; i < listResponseEntity.size(); i++) {
                        if (count == listResponseEntity.size()
                                && !profileEntity.getId().equals(listResponseEntity.get(i).getId())) {
                            listResponseEntity.add(profileEntity);
                        }
                    }
                }
            }
        }

        Sort sortObj = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sortObj);
        Page<UserProfileEntity> page =
                new PageImpl<>(listResponseEntity, pageable, listResponseEntity.size());
        PageResponseProfile pageResponse = pagingProfile(page);
        return pageResponse;
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
                profileEntity.setAvatarPhoto("uploaded-images/user-avatars/default/default-avt.png");
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
                profileEntity.setCoverPhoto("uploaded-images/user-covers/default-background.jpg");
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

    private PageResponseProfile pagingProfile(Page<UserProfileEntity> profileEntities) {
        List<UserProfileEntity> profileEntityList = profileEntities.getContent();

        List<UserProfileDTO> contentList = new ArrayList<>();
        for (UserProfileEntity profileEntity : profileEntityList) {
            UserProfileDTO profileDTO = converter.toDto(profileEntity);
            setUrlAvartarAndCover(profileEntity, profileDTO);
            contentList.add(profileDTO);
        }

        PageResponseProfile pageResponse = new PageResponseProfile();
        pageResponse.setPageNo(profileEntities.getNumber());
        pageResponse.setContent(contentList);
        pageResponse.setPageSize(profileEntities.getSize());
        pageResponse.setTotalPages(profileEntities.getTotalPages());
        pageResponse.setTotalElements((int) profileEntities.getTotalElements());
        pageResponse.setLast(profileEntities.isLast());

        return pageResponse;
    }
}

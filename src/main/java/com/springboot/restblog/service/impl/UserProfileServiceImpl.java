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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

        return converter.toDto(userProfile);
    }

    @Override
    public UserProfileDTO setInfoUser(Integer userId, UserProfileDTO userProfileDTO) {  //temporary logic

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Integer id = customUser.getUserId();

        if (!id.equals(userId)) {
            throw new APIException(HttpStatus.BAD_REQUEST, "This user isn't the owner!");
        }

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        UserProfileEntity oldProfile = userProfileRepository.findUserProfileEntityByUser(userEntity).get();

        UserProfileEntity newProfile = userProfileRepository.save(converter.toEntity(oldProfile, userProfileDTO));

        return converter.toDto(newProfile);
    }
}

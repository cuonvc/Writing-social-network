package com.springboot.restblog.service.impl;

import com.springboot.restblog.exception.APIException;
import com.springboot.restblog.model.entity.RoleEntity;
import com.springboot.restblog.model.entity.UserEntity;
import com.springboot.restblog.model.entity.UserProfileEntity;
import com.springboot.restblog.model.payload.RegisterDTO;
import com.springboot.restblog.repository.RoleRepository;
import com.springboot.restblog.repository.UserProfileRepository;
import com.springboot.restblog.repository.UserRepository;
import com.springboot.restblog.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void createUser(RegisterDTO registerDTO) {
        UserEntity user = setField(registerDTO);
        RoleEntity role = roleRepository.findByName("ROLE_USER").get();

        Set<RoleEntity> roleEntities = new HashSet<>();
        roleEntities.add(role);
        user.setRoles(roleEntities);

        userRepository.save(user);
    }

    @Override
    public void createAdmin(RegisterDTO registerDTO) {
        UserEntity user = setField(registerDTO);
        RoleEntity role = roleRepository.findByName("ROLE_ADMIN").get();

        Set<RoleEntity> roleEntities = new HashSet<>();
        roleEntities.add(role);
        user.setRoles(roleEntities);

        userRepository.save(user);
    }

    private UserEntity setField(RegisterDTO registerDTO) {
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Username is already taken");
        }

        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Email is already taken");
        }

        UserEntity user = new UserEntity();

        user.setFirstName(registerDTO.getFirstName());
        user.setLastName(registerDTO.getLastName());
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setDateRegistered(new Date());

        UserProfileEntity userProfile = new UserProfileEntity();
        userProfile.setUser(user);
        userProfile.setFullName(registerDTO.getFirstName() + " " + registerDTO.getLastName());

        userProfileRepository.save(userProfile);

        return user;
    }
}

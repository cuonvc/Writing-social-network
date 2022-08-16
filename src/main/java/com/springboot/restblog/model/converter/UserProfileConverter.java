package com.springboot.restblog.model.converter;

import com.springboot.restblog.model.entity.RoleEntity;
import com.springboot.restblog.model.entity.UserEntity;
import com.springboot.restblog.model.entity.UserProfileEntity;
import com.springboot.restblog.model.payload.RoleDTO;
import com.springboot.restblog.model.payload.UserProfileDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class UserProfileConverter {

    @Autowired
    private ModelMapper mapper;

    public UserProfileDTO toDto(UserProfileEntity entity) {

//        String fullName = entity.getFullName();
//        String[] array = fullName.split(" ");

        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setFullName(entity.getFirstName() + " " + entity.getLastName());
        dto.setGender(entity.getGender());
        dto.setAbout(entity.getAbout());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setAvatarPhoto(entity.getAvatarPhoto());
        dto.setCoverPhoto(entity.getCoverPhoto());

        UserEntity userEntity = entity.getUser();
        dto.setEmailByUser(userEntity.getEmail());
        dto.setUsernameByUser(userEntity.getUsername());

        Set<RoleDTO> roleDTOS = new HashSet<>();
        RoleConverter roleConverter = new RoleConverter();
        for (RoleEntity roleEntity : userEntity.getRoles()) {
            RoleDTO roleDTO = roleConverter.toDto(roleEntity);
            roleDTOS.add(roleDTO);
        }
        dto.setRoles(roleDTOS);


        return dto;
    }

//    public UserProfileEntity toEntity(UserProfileDTO dto) {
//        UserProfileEntity entity = mapper.map(dto,UserProfileEntity.class);
//
//        return entity;
//    }

    public UserProfileEntity toEntity(UserProfileEntity entity, UserProfileDTO dto) {
        //custom
//        entity.setFullName(dto.getFirstName() + " " + dto.getLastName());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setGender(dto.getGender());
        entity.setDateOfBirth(dto.getDateOfBirth());
        entity.setAbout(dto.getAbout());

        return entity;
    }
}

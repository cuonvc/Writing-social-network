package com.springboot.restblog.model.converter;

import com.springboot.restblog.model.entity.UserProfileEntity;
import com.springboot.restblog.model.payload.UserProfileDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserProfileConverter {

    @Autowired
    private ModelMapper mapper;

    public UserProfileDTO toDto(UserProfileEntity entity) {

        String fullName = entity.getFullName();
        String[] array = fullName.split(" ");

        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(entity.getId());
        dto.setFirstName(array[0]);
        dto.setLastName(array[1]);
        dto.setFullName(entity.getFullName());
        dto.setGender(entity.getGender());
        dto.setAbout(entity.getAbout());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setAvatarPhoto(entity.getAvatarPhoto());
        dto.setCoverPhoto(entity.getCoverPhoto());

        return dto;
    }

//    public UserProfileEntity toEntity(UserProfileDTO dto) {
//        UserProfileEntity entity = mapper.map(dto,UserProfileEntity.class);
//
//        return entity;
//    }

    public UserProfileEntity toEntity(UserProfileEntity entity, UserProfileDTO dto) {
        //custom
        entity.setFullName(dto.getFirstName() + " " + dto.getLastName());
        entity.setGender(dto.getGender());
        entity.setDateOfBirth(dto.getDateOfBirth());
        entity.setAbout(dto.getAbout());

        return entity;
    }
}

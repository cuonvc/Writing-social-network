package com.springboot.restblog.model.converter;

import com.springboot.restblog.model.entity.RoleEntity;
import com.springboot.restblog.model.payload.RoleDTO;
import org.springframework.stereotype.Component;

@Component
public class RoleConverter {

    public RoleDTO toDto(RoleEntity entity) {
        RoleDTO dto = new RoleDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());

        return dto;
    }

    public RoleEntity toEntity(RoleDTO dto) {
        RoleEntity entity = new RoleEntity();
        entity.setName(dto.getName());

        return entity;
    }

    public RoleEntity toEntity(RoleDTO dto, RoleEntity entity) {
        entity.setName(dto.getName());

        return entity;
    }
}

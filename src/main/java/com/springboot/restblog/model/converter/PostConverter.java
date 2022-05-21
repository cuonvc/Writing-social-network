package com.springboot.restblog.model.converter;

import com.springboot.restblog.model.entity.PostEntity;
import com.springboot.restblog.model.payload.PostDTO;
import org.springframework.stereotype.Component;

@Component
public class PostConverter {

    //convert DTO to entity
    public PostEntity toEntity(PostDTO dto) {
        PostEntity entity = new PostEntity();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setContent(dto.getContent());

        return entity;
    }

    //convert entity to DTO
    public PostDTO toDTO(PostEntity entity) {
        PostDTO dto = new PostDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getContent());
        dto.setContent(entity.getContent());

        return dto;
    }
}

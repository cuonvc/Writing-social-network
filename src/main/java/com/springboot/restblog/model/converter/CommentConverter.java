package com.springboot.restblog.model.converter;

import com.springboot.restblog.model.entity.CommentEntity;
import com.springboot.restblog.model.payload.CommentDTO;
import org.springframework.stereotype.Component;

@Component
public class CommentConverter {

    public CommentDTO toDTO(CommentEntity entity) {
        CommentDTO dto = new CommentDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setContent(entity.getContent());

        return dto;
    }

    public CommentEntity toEntity(CommentDTO dto) {
        CommentEntity entity = new CommentEntity();
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setContent(dto.getContent());

        return entity;
    }
}

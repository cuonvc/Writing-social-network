package com.springboot.restblog.model.converter;

import com.springboot.restblog.model.entity.CommentEntity;
import com.springboot.restblog.model.payload.CommentDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentConverter {

    @Autowired
    private ModelMapper mapper;

    public CommentConverter(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public CommentDTO toDTO(CommentEntity entity) {
//        CommentDTO dto = new CommentDTO();
//        dto.setId(entity.getId());
//        dto.setName(entity.getName());
//        dto.setEmail(entity.getEmail());
//        dto.setContent(entity.getContent());

        //replace
        CommentDTO dto = mapper.map(entity, CommentDTO.class);

        return dto;
    }

    public CommentEntity toEntity(CommentDTO dto) {
//        CommentEntity entity = new CommentEntity();
//        entity.setName(dto.getName());
//        entity.setEmail(dto.getEmail());
//        entity.setContent(dto.getContent());

        //replace
        CommentEntity entity = mapper.map(dto, CommentEntity.class);

        return entity;
    }

    public CommentEntity toEntity(CommentEntity entity, CommentDTO dto) {
//        entity.setName(dto.getName());
//        entity.setEmail(dto.getEmail());
//        entity.setContent(dto.getContent());

        //replace
        entity = mapper.map(dto, CommentEntity.class);

        return entity;
    }
}

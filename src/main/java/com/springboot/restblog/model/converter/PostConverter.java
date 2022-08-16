package com.springboot.restblog.model.converter;

import com.springboot.restblog.model.entity.CommentEntity;
import com.springboot.restblog.model.entity.PostEntity;
import com.springboot.restblog.model.payload.CommentDTO;
import com.springboot.restblog.model.payload.PostDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class PostConverter {

    //nếu sử dụng modelMapper thì các fields của entity và dto phải giống nhau

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private CommentConverter commentConverter;

    public PostConverter(ModelMapper mapper) {
        this.mapper = mapper;
    }

    //convert DTO to entity
    public PostEntity toEntity(PostDTO dto) {
        PostEntity entity = new PostEntity();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setContent(dto.getContent());

        //replace
//        PostEntity entity = mapper.map(dto, PostEntity.class);

        return entity;
    }

    public PostEntity toEntity(PostDTO dto, PostEntity entity) {
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setContent(dto.getContent());

        //replace
//        entity = mapper.map(dto, PostEntity.class);

        return entity;
    }

    //convert entity to DTO
    public PostDTO toDTO(PostEntity entity) {
        //Manually convert
//        PostDTO dto = new PostDTO();
//
//        Set<CommentDTO> commentDTOS = new HashSet<>();
//        for (CommentEntity comment : entity.getComments()) {
//            commentDTOS.add(commentConverter.toDTO(comment));
//        }
//
//        dto.setId(entity.getId());
//        dto.setTitle(entity.getTitle());
//        dto.setDescription(entity.getContent());
//        dto.setContent(entity.getContent());
//        dto.setComments(commentDTOS);

        //repalce (Convert by modelMapper lib)
        PostDTO dto = mapper.map(entity, PostDTO.class);
        dto.setThumbnails(entity.getThumbnails());
        dto.getUserProfile().setUsernameByUser(entity.getUserProfile().getUser().getUsername());
        dto.getUserProfile().setEmailByUser(entity.getUserProfile().getUser().getEmail());

        return dto;
    }
}

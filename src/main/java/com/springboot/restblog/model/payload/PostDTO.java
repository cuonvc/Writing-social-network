package com.springboot.restblog.model.payload;

import com.springboot.restblog.model.entity.CommentEntity;
import lombok.Data;

import java.util.Set;

@Data
public class PostDTO {

    private Integer id;
    private String title;
    private String description;
    private String content;
    private Set<CommentDTO> comments;
}

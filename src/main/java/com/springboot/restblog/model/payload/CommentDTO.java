package com.springboot.restblog.model.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CommentDTO {

    private Integer id;
    private String name;
    private String email;
    private String content;

}

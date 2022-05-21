package com.springboot.restblog.model.payload;

import lombok.Data;

@Data
public class PostDTO {

    private long id;
    private String title;
    private String description;
    private String content;
}

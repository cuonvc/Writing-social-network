package com.springboot.restblog.model.payload;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
public class CommentDTO {

    private Integer id;

    @NotEmpty(message = "Content should not be null or empty")
    private String content;

    private Date createdDate;
    private Date modifiedDate;

}

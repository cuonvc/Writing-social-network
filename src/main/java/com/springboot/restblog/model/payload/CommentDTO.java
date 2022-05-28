package com.springboot.restblog.model.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class CommentDTO {

    private Integer id;

    @NotEmpty
    @Size(min = 2, max = 30, message = "Name must be minium 2 characters and maximun 30 characters")
    private String name;

    @NotEmpty
    @Email
    private String email;

    @NotEmpty(message = "Content should not be null or empty")
    private String content;

}

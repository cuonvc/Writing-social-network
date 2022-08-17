package com.springboot.restblog.model.payload;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserDTO {

    private Integer id;

    @NotEmpty
    @Email(message = "Email invalidate!")
    private String email;

    @NotBlank
    @NotEmpty
    @Size(min = 6, max = 30, message = "Username must be minium 6 characters and maximum 30 characters")
    private String username;
}

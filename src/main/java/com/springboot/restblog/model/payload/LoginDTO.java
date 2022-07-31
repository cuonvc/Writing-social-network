package com.springboot.restblog.model.payload;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
public class LoginDTO {

    @NotEmpty
    @NotBlank
    @Size(max = 70, message = "username or email must be maximum 70 characters")
    private String usernameOrEmail;

    @NotEmpty
    @NotBlank
    @Size(max = 30, message = "password must be maximum 30 characters")
    private String password;
}

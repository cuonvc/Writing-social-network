package com.springboot.restblog.model.payload;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class LoginDTO {

    @NotEmpty
    @NotBlank
    private String usernameOrEmail;

    @NotEmpty
    @NotBlank
    private String password;
}

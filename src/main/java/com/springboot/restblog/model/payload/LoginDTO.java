package com.springboot.restblog.model.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {

    private String usernameOrEmail;
    private String password;
}

package com.springboot.restblog.model.payload;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RegisterDTO {
    private String name;
    private String username;
    private String email;
    private String password;
}

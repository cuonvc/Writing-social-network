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
    @Size(min = 2, max = 10, message = "First name must be minium 2 characters and maximum 10 characters")
    private String firstName;

    @Size(min = 2, max = 10, message = "Last name must be minium 2 characters and maximum 10 characters")
    @NotEmpty
    private String lastName;

    @NotEmpty
    @Email(message = "Email invalidate!")
    private String email;

    @NotBlank
    @NotEmpty
    private String username;
}

package com.springboot.restblog.model.payload;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Getter
@Setter
public class RegisterDTO {

    @NotEmpty
    @NotBlank
    @Size(min = 2, max = 20, message = "First name must be minium 2 characters and maximum 20 characters")
    private String firstName;

    @NotEmpty
    @NotBlank
    @Size(min = 2, max = 20, message = "Last name must be minium 2 characters and maximum 20 characters")
    private String lastName;

    @NotEmpty
    @NotBlank
    @Size(min = 6, max = 30, message = "Username must be minium 6 characters and maximum 30 characters")
    private String username;

    @NotEmpty
    @NotBlank
    @Email(message = "Email invalid")
    @Size(max = 70, message = "Email must be maximum 70 characters")
    private String email;

    @NotEmpty
    @NotBlank
    @Size(min = 2, max = 30, message = "Password must be minium 2 characters and maximum 30 characters")
    private String password;
}

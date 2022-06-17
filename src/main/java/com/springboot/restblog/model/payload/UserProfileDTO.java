package com.springboot.restblog.model.payload;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
public class UserProfileDTO {

    private Integer id;

    private String firstName;
    private String lastName;
    private String fullName;
    private String gender;
    private Date dateOfBirth;
    private String avatarPhoto;
    private String coverPhoto;

    @NotEmpty
    @Size(min = 10, max = 100, message = "About must minium 10 characters and maximum 100 characters")
    private String about;
}

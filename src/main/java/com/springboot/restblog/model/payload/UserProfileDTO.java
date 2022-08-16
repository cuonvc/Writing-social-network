package com.springboot.restblog.model.payload;

import com.springboot.restblog.anotation.ValidImage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class UserProfileDTO {

    private Integer id;
    private String emailByUser;
    private String usernameByUser;
    private List<RoleDTO> roles;
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

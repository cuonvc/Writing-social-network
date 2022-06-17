package com.springboot.restblog.service;

import com.springboot.restblog.model.entity.UserEntity;
import com.springboot.restblog.model.payload.RegisterDTO;
import com.springboot.restblog.model.payload.UserDTO;

public interface IAuthService {
    void createUser(RegisterDTO registerDTO);
    void createAdmin(RegisterDTO registerDTO);
}

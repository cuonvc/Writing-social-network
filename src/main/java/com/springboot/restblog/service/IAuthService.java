package com.springboot.restblog.service;

import com.springboot.restblog.model.payload.RegisterDTO;

public interface IAuthService {
    void createUser(RegisterDTO registerDTO);
    void createAdmin(RegisterDTO registerDTO);
}

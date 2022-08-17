package com.springboot.restblog.service;

import com.springboot.restblog.model.payload.RoleDTO;
import java.util.List;

public interface IRoleService {
    RoleDTO saveRole(RoleDTO roleDTO);
    List<RoleDTO> findAllRole();
    RoleDTO findById(Integer id);
    void deleteRole(Integer id);
}

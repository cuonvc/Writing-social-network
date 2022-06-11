package com.springboot.restblog.service.impl;

import com.springboot.restblog.exception.APIException;
import com.springboot.restblog.exception.ResourceNotFoundException;
import com.springboot.restblog.model.converter.RoleConverter;
import com.springboot.restblog.model.entity.RoleEntity;
import com.springboot.restblog.model.entity.UserEntity;
import com.springboot.restblog.model.payload.RoleDTO;
import com.springboot.restblog.repository.RoleRepository;
import com.springboot.restblog.repository.UserRepository;
import com.springboot.restblog.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements IRoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleConverter converter;

    @Override
    public RoleDTO saveRole(RoleDTO roleDTO) {
        List<RoleEntity> roleEntities = roleRepository.findAll();
        for (RoleEntity roleEntity : roleEntities) {
            if (roleEntity.getName().compareTo(roleDTO.getName()) == 0) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Role already exists");
            }
        }

        RoleEntity roleEntity;
        if (roleDTO.getId() == null) {
            //create
            roleEntity = converter.toEntity(roleDTO);
        } else {
            //update
            RoleEntity oldRole = roleRepository.findById(roleDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleDTO.getId()));
            roleEntity = converter.toEntity(roleDTO, oldRole);
        }

        RoleEntity newRole = roleRepository.save(roleEntity);
        return converter.toDto(newRole);
    }

    @Override
    public List<RoleDTO> findAllRole() {
        List<RoleDTO> listResponse = roleRepository.findAll()
                .stream().map(role -> converter.toDto(role)).collect(Collectors.toList());
        return listResponse;
    }

    @Override
    public RoleDTO findById(Integer id) {
        RoleEntity roleEntity = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        return converter.toDto(roleEntity);
    }

    @Override
    public void deleteRole(Integer id) {
        RoleEntity roleRemove = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));

        List<UserEntity> userEntityList = userRepository.findAll();
        for (UserEntity userEntity : userEntityList) {
            Set<RoleEntity> roleEntities = userEntity.getRoles();

            for (RoleEntity roleEntity : roleEntities) {
                if (roleEntity.getId() == id) {
                    throw new APIException(HttpStatus.BAD_REQUEST,
                            "This role can't be deleted because there are users that apply!");
                }
            }
        }
        roleRepository.delete(roleRemove);
    }
}

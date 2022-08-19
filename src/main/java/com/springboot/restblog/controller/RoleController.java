package com.springboot.restblog.controller;

import com.springboot.restblog.model.payload.RoleDTO;
import com.springboot.restblog.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 31536000)
@RequestMapping("/api/v1")
public class RoleController {

    @Autowired
    private IRoleService roleService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/role")
    public ResponseEntity<RoleDTO> createRole(@RequestBody RoleDTO roleDTO) {
        RoleDTO roleResponse = roleService.saveRole(roleDTO);
        return new ResponseEntity<>(roleResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/role/{id}")
    public ResponseEntity<RoleDTO> updateRole(@PathVariable(name = "id") Integer id,
                                              @RequestBody RoleDTO roleDTO) {
        roleDTO.setId(id);
        RoleDTO roleRespose = roleService.saveRole(roleDTO);

        return new ResponseEntity<>(roleRespose, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/roles")
    public List<RoleDTO> getAllRole() {
        List<RoleDTO> response = roleService.findAllRole();
        return response;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/role/{id}")
    public ResponseEntity<RoleDTO> getById(@PathVariable(name = "id") Integer id) {
        RoleDTO roleResponse = roleService.findById(id);
        return new ResponseEntity<>(roleResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/role/{id}")
    public ResponseEntity<String> deleteById(@PathVariable(name = "id") Integer id) {
        roleService.deleteRole(id);
        return new ResponseEntity<>("Delete role successfully!", HttpStatus.OK);
    }
}

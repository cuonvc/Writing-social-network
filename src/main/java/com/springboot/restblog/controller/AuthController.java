package com.springboot.restblog.controller;

import com.springboot.restblog.model.entity.RoleEntity;
import com.springboot.restblog.model.entity.UserEntity;
import com.springboot.restblog.model.payload.CustomUser;
import com.springboot.restblog.model.payload.JwtAuthResponse;
import com.springboot.restblog.model.payload.LoginDTO;
import com.springboot.restblog.model.payload.RegisterDTO;
import com.springboot.restblog.repository.RoleRepository;
import com.springboot.restblog.repository.UserRepository;
import com.springboot.restblog.security.JwtTokenProvider;
import com.springboot.restblog.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private IAuthService authService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> authenticateUser(@Valid @RequestBody LoginDTO loginDTO) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsernameOrEmail(), loginDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        //get token from JwtTokenProvider
        String token = jwtTokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new JwtAuthResponse(token));
    }

    @PostMapping("/user/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterDTO registerDTO) {
        authService.createUser(registerDTO);

        return new ResponseEntity<>("User register successfully!", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/admin/signup")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody RegisterDTO registerDTO) {
        authService.createAdmin(registerDTO);

        return new ResponseEntity<>("Admin register successfully!", HttpStatus.OK);
    }

}

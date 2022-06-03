package com.springboot.restblog.model.payload;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUser extends User {
    private final Integer userId;

    public CustomUser(String email, String password, Collection<? extends GrantedAuthority> mapRolesToAuthorities, Integer userId) {
        super(email, password, mapRolesToAuthorities);
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }
}

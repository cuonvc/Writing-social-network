package com.springboot.restblog.model.payload;

import com.springboot.restblog.model.entity.RoleEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;

public class CustomUser extends User {
    private final Integer userId;
    private final Set<RoleEntity> roles;

    public CustomUser(String email, String password, Collection<? extends GrantedAuthority> mapRolesToAuthorities,
                      Integer userId, Set<RoleEntity> roles) {
        super(email, password, mapRolesToAuthorities);
        this.userId = userId;
        this.roles = roles;
    }

    public Integer getUserId() {
        return userId;
    }

    public Set<RoleEntity> getRoles() {
        return roles;
    }
}

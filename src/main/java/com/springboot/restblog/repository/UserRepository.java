package com.springboot.restblog.repository;

import com.springboot.restblog.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByUsernameOrEmail(String username, String email);
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
}

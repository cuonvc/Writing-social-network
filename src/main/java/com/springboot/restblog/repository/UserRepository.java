package com.springboot.restblog.repository;

import com.springboot.restblog.model.entity.RoleEntity;
import com.springboot.restblog.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    //https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#appendix.query.method.subject

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByUsernameOrEmail(String username, String email);
    List<UserEntity> findUserEntitiesByRoles(RoleEntity role);

    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
}

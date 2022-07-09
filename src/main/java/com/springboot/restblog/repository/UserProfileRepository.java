package com.springboot.restblog.repository;

import com.springboot.restblog.model.entity.UserEntity;
import com.springboot.restblog.model.entity.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfileEntity, Integer> {
    Optional<UserProfileEntity> findUserProfileEntityByUser(UserEntity userEntity);

    @Query("SELECT u FROM UserProfileEntity u WHERE " +
            "u.firstName LIKE CONCAT('%', :query, '%') " +
            "or u.lastName LIKE CONCAT('%', :query, '%') ")
    List<UserProfileEntity> searchProfiles(String query);
}

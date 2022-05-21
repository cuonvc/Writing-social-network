package com.springboot.restblog.repository;

import com.springboot.restblog.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Integer> {

}

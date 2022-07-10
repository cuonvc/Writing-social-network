package com.springboot.restblog.repository;

import com.springboot.restblog.model.entity.CategoryEntity;
import com.springboot.restblog.model.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Integer> {
    Page<PostEntity> findPostEntityByCategoryEntities(CategoryEntity category, Pageable pageable);

    @Query("SELECT p FROM PostEntity p WHERE " +
            "p.title LIKE CONCAT('%', :query, '%')")
    List<PostEntity> searchPosts(String query);
}

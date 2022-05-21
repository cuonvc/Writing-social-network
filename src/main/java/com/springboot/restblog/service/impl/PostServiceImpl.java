package com.springboot.restblog.service.impl;

import com.springboot.restblog.model.converter.PostConverter;
import com.springboot.restblog.model.entity.PostEntity;
import com.springboot.restblog.model.payload.PostDTO;
import com.springboot.restblog.repository.PostRepository;
import com.springboot.restblog.service.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements IPostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostConverter converter;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public PostDTO savePost(PostDTO postDTO) {
        PostEntity postEntity = converter.toEntity(postDTO);
        PostEntity newPost = postRepository.save(postEntity);

        PostDTO postResponse = converter.toDTO(newPost);
        return postResponse;
    }
}
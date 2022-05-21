package com.springboot.restblog.service.impl;

import com.springboot.restblog.exception.ResourceNotFoundException;
import com.springboot.restblog.model.converter.PostConverter;
import com.springboot.restblog.model.entity.PostEntity;
import com.springboot.restblog.model.payload.PostDTO;
import com.springboot.restblog.repository.PostRepository;
import com.springboot.restblog.service.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

        PostEntity postEntity;

        if (postDTO.getId() != null) {  //post is existed
            //update
            PostEntity oldPost = postRepository.findById(postDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postDTO.getId()));

            postEntity = converter.toEntity(postDTO, oldPost);
        } else {
            //create
            postEntity = converter.toEntity(postDTO);
        }

        PostEntity newPost = postRepository.save(postEntity);
        return converter.toDTO(newPost);
    }

    @Override
    public List<PostDTO> getAll() {
        List<PostEntity> postEntities = postRepository.findAll();

        return postEntities.stream()
                .map(post -> converter.toDTO(post))
                .collect(Collectors.toList());
    }

    @Override
    public PostDTO getById(Integer id) {
        PostEntity postEntity = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        return converter.toDTO(postEntity);
    }

    @Override
    public void deleteById(Integer id) {
        PostEntity postResponse = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        postRepository.delete(postResponse);
    }
}

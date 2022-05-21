package com.springboot.restblog.service;

import com.springboot.restblog.model.payload.PostDTO;

import java.util.List;

public interface IPostService {

    PostDTO savePost(PostDTO postDTO);
    List<PostDTO> getAll();
}

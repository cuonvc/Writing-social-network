package com.springboot.restblog.service;

import com.springboot.restblog.model.payload.PostDTO;

import java.util.List;

public interface IPostService {

    PostDTO savePost(PostDTO postDTO);
    List<PostDTO> getAll(Integer pageNo, Integer pageSize);
    PostDTO getById(Integer id);
    void deleteById(Integer id);
}

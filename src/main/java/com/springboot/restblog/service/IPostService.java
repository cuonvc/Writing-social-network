package com.springboot.restblog.service;

import com.springboot.restblog.model.payload.PostDTO;
import com.springboot.restblog.model.payload.PostResponse;

import java.util.List;

public interface IPostService {

    PostDTO savePost(Integer userId, PostDTO postDTO);
    PostResponse getAll(Integer pageNo, Integer pageSize, String sortBy, String sortDir);
    PostDTO getById(Integer id);
    void deleteById(Integer userId, Integer id);
}

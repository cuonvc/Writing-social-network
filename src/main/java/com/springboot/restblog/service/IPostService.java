package com.springboot.restblog.service;

import com.springboot.restblog.model.payload.PostDTO;

public interface IPostService {

    PostDTO savePost(PostDTO postDTO);
}

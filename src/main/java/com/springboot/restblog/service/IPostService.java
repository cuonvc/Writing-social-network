package com.springboot.restblog.service;

import com.springboot.restblog.model.payload.PostDTO;
import com.springboot.restblog.model.payload.PostResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IPostService {

    PostDTO savePost(Integer[] categoryIds, PostDTO postDTO, MultipartFile file) throws IOException;
    PostDTO editPost(PostDTO postDTO);
    PostResponse getAll(Integer pageNo, Integer pageSize, String sortBy, String sortDir);
    PostResponse getByCategory(Integer categoryId, Integer pageNo, Integer pageSize, String sortBy, String sortDir);
    PostDTO getById(Integer id);
    void deleteById(Integer id);
}

package com.springboot.restblog.service;

import com.springboot.restblog.model.payload.PostDTO;
import com.springboot.restblog.model.payload.PageResponsePost;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface IPostService {

    PostDTO savePost(Integer[] categoryIds, PostDTO postDTO) ;
    PostDTO editPost(PostDTO postDTO);
    String saveImageByPost(MultipartFile file) throws IOException;
    PageResponsePost getAll(Integer pageNo, Integer pageSize, String sortBy, String sortDir);
    PageResponsePost getByCategory(Integer categoryId, Integer pageNo, Integer pageSize, String sortBy, String sortDir);
    PageResponsePost filterByKeyword(String keyword, Integer pageNo, Integer pageSize, String sortBy, String sortDir);
    PostDTO getById(Integer id);
    void deleteById(Integer id);
}

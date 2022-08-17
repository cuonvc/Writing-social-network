package com.springboot.restblog.service;

import com.springboot.restblog.model.payload.PostDTO;
import com.springboot.restblog.model.payload.PageResponsePost;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IPostService {

    PostDTO savePost(Integer[] categoryIds, PostDTO postDTO) ;
    PostDTO editPost(PostDTO postDTO);
    void pinOrUnpinPost(Integer id, Boolean pinned);
    String saveImageByPost(MultipartFile file) throws IOException;
    PageResponsePost getAll(Integer pageNo, Integer pageSize, String sortBy, String sortDir);
    PageResponsePost getByCategory(Integer categoryId, Integer pageNo, Integer pageSize, String sortBy, String sortDir);
    PageResponsePost filterByKeyword(String keyword, Integer pageNo, Integer pageSize, String sortBy, String sortDir);
    PostDTO getById(Integer id);
    void deleteById(Integer id);
}

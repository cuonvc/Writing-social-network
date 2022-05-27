package com.springboot.restblog.service;

import com.springboot.restblog.model.payload.CommentDTO;

import java.util.List;

public interface ICommentService {

    CommentDTO saveComment(Integer idPost, CommentDTO commentDTO);
    CommentDTO getById(Integer id, Integer idPost);
    List<CommentDTO> getCommentsByPostId(Integer id);
    void deleteById(Integer id, Integer idPost);
}

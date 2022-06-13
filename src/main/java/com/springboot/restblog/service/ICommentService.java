package com.springboot.restblog.service;

import com.springboot.restblog.model.payload.CommentDTO;

import java.util.List;

public interface ICommentService {

    CommentDTO saveComment(Integer postId, CommentDTO commentDTO);
    CommentDTO updateCommentById(CommentDTO commentDTO);
    CommentDTO getById(Integer id);
    List<CommentDTO> getCommentsByPostId(Integer id);
    void deleteById(Integer id);
}

package com.springboot.restblog.service;

import com.springboot.restblog.model.payload.CommentDTO;

public interface ICommentService {

    CommentDTO saveComment(Integer idPost, CommentDTO commentDTO);
}

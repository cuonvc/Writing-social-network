package com.springboot.restblog.service.impl;

import com.springboot.restblog.exception.ResourceNotFoundException;
import com.springboot.restblog.model.converter.CommentConverter;
import com.springboot.restblog.model.entity.CommentEntity;
import com.springboot.restblog.model.entity.PostEntity;
import com.springboot.restblog.model.payload.CommentDTO;
import com.springboot.restblog.repository.CommentRepository;
import com.springboot.restblog.repository.PostRepository;
import com.springboot.restblog.service.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements ICommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentConverter converter;

    @Override
    public CommentDTO saveComment(Integer idPost, CommentDTO commentDTO) {
        CommentEntity commentEntity;

        if (commentDTO.getId() == null) {
            //save
            commentEntity = converter.toEntity(commentDTO);
        } else {
            //update
            CommentEntity oldComment = commentRepository.findById(commentDTO.getId()).
                    orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentDTO.getId()));
            commentEntity = converter.toEntity(oldComment, commentDTO);
        }

        commentEntity.setPost(postRepository.findById(idPost).
                orElseThrow(() -> new ResourceNotFoundException("Post", "id", idPost)));

        CommentEntity newComment = commentRepository.save(commentEntity);

        return converter.toDTO(newComment);
    }
}

package com.springboot.restblog.service.impl;

import com.springboot.restblog.exception.APIException;
import com.springboot.restblog.exception.ResourceNotFoundException;
import com.springboot.restblog.model.converter.CommentConverter;
import com.springboot.restblog.model.entity.CommentEntity;
import com.springboot.restblog.model.entity.PostEntity;
import com.springboot.restblog.model.payload.CommentDTO;
import com.springboot.restblog.repository.CommentRepository;
import com.springboot.restblog.repository.PostRepository;
import com.springboot.restblog.service.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
            CommentEntity oldComment = commentRepository.findById(commentDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentDTO.getId()));

            commentEntity = converter.toEntity(oldComment, commentDTO);
        }

        commentEntity.setPost(postRepository.findById(idPost)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", idPost)));

        CommentEntity newComment = commentRepository.save(commentEntity);

        return converter.toDTO(newComment);
    }

    public CommentDTO getById(Integer id, Integer idPost) {

        CommentEntity commentById = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

        PostEntity postById = postRepository.findById(idPost)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", idPost));

        if (!commentById.getPost().getId().equals(postById.getId())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Comment does not belong to post");
        }

        return converter.toDTO(commentById);
    }

    @Override
    public List<CommentDTO> getCommentsByPostId(Integer idPost) {
        List<CommentEntity> entityList = commentRepository.findByPostId(idPost);

        List<CommentDTO> commentList = entityList.stream().map(comment -> converter.toDTO(comment))
                .collect(Collectors.toList());

        return commentList;
    }

    @Override
    public void deleteById(Integer id, Integer idPost) {
        CommentEntity commentById = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

        PostEntity postById = postRepository.findById(idPost)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", idPost));

        if (!commentById.getPost().getId().equals(postById.getId())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Comment does not belong to post");
        }

        commentRepository.delete(commentById);
    }


}

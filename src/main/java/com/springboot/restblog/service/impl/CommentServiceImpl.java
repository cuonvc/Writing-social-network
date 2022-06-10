package com.springboot.restblog.service.impl;

import com.springboot.restblog.exception.APIException;
import com.springboot.restblog.exception.ResourceNotFoundException;
import com.springboot.restblog.model.converter.CommentConverter;
import com.springboot.restblog.model.entity.CommentEntity;
import com.springboot.restblog.model.entity.PostEntity;
import com.springboot.restblog.model.entity.UserEntity;
import com.springboot.restblog.model.payload.CommentDTO;
import com.springboot.restblog.model.payload.CustomUser;
import com.springboot.restblog.repository.CommentRepository;
import com.springboot.restblog.repository.PostRepository;
import com.springboot.restblog.repository.UserRepository;
import com.springboot.restblog.service.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements ICommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentConverter converter;

    public CommentServiceImpl(CommentRepository commentRepository,
                              PostRepository postRepository,
                              CommentConverter converter) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.converter = converter;
    }

    @Override
    public CommentDTO saveComment(Integer userId, Integer idPost, CommentDTO commentDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser user = (CustomUser) authentication.getPrincipal();
        Integer id = user.getUserId();

        if (!id.equals(userId)) {
            throw new APIException(HttpStatus.BAD_REQUEST, "User do not allow access this comment");
        }

        PostEntity postById = postRepository.findById(idPost)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", idPost));
        UserEntity userById = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        CommentEntity commentEntity;

        if (commentDTO.getId() == null) {
            //create
            commentEntity = converter.toEntity(commentDTO);
        } else {
            //update
            CommentEntity oldComment = commentRepository.findById(commentDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentDTO.getId()));

            commentEntity = converter.toEntity(oldComment, commentDTO);

            CommentEntity commentById = commentRepository.findById(commentDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentDTO.getId()));

            if (!commentById.getPost().getId().equals(postById.getId())) {
                throw new APIException(HttpStatus.BAD_REQUEST,"Comment do not belong this post");
            }

            if (!commentById.getUser().getId().equals(userById.getId())) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Comment do not belong this User");
            }
        }
        commentEntity.setUser(userById);
        commentEntity.setPost(postById);

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
        PostEntity postEntity = postRepository.findById(idPost)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", idPost));

        Set<CommentEntity> commentEntities = postEntity.getComments();

        List<CommentDTO> commentList = commentEntities.stream().map(comment -> converter.toDTO(comment))
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

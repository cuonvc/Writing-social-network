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

import java.util.Date;
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

    @Override
    public CommentDTO saveComment(Integer postId, CommentDTO commentDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        PostEntity postById = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        CommentEntity commentEntity;
        commentEntity = converter.toEntity(commentDTO);

        String emailByUser = authentication.getName();
        UserEntity userByEmail = userRepository.findByEmail(emailByUser).get();

        commentEntity.setUserProfile(userByEmail.getUserProfile());
        commentEntity.setPost(postById);
        commentEntity.setCreatedDate(new Date());
        commentEntity.setModifiedDate(new Date());

        CommentEntity newComment = commentRepository.save(commentEntity);

        return converter.toDTO(newComment);
    }

    @Override
    public CommentDTO updateCommentById(CommentDTO commentDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailClient = authentication.getName();

        CommentEntity oldComment = commentRepository.findById(commentDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentDTO.getId()));
        String emailOwner = oldComment.getUserProfile().getUser().getEmail();
        PostEntity postByComment = oldComment.getPost();

        if (!emailClient.equals(emailOwner)) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Comment does not belong to this user");
        }

        UserEntity userByEmail = userRepository.findByEmail(emailClient).get();

        CommentEntity commentEntity = converter.toEntity(oldComment, commentDTO);
        commentEntity.setUserProfile(userByEmail.getUserProfile());
        commentEntity.setPost(postByComment);
        commentEntity.setCreatedDate(oldComment.getCreatedDate());
        commentEntity.setModifiedDate(new Date());

        CommentEntity newComment = commentRepository.save(commentEntity);
        return converter.toDTO(newComment);
        //chưa test save and update comment và chỉnh sửa controller các method dưới
    }

    public CommentDTO getById(Integer id) {
        CommentEntity commentById = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

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
    public void deleteById(Integer id) {
        CommentEntity commentById = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

        commentRepository.delete(commentById);
    }
}

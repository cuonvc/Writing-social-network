package com.springboot.restblog.service.impl;

import com.springboot.restblog.exception.APIException;
import com.springboot.restblog.exception.ResourceNotFoundException;
import com.springboot.restblog.model.converter.CommentConverter;
import com.springboot.restblog.model.entity.CommentEntity;
import com.springboot.restblog.model.entity.PostEntity;
import com.springboot.restblog.model.entity.UserEntity;
import com.springboot.restblog.model.payload.CommentDTO;
import com.springboot.restblog.model.payload.CustomUser;
import com.springboot.restblog.model.payload.PostDTO;
import com.springboot.restblog.model.payload.UserProfileDTO;
import com.springboot.restblog.repository.CommentRepository;
import com.springboot.restblog.repository.PostRepository;
import com.springboot.restblog.repository.UserRepository;
import com.springboot.restblog.service.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
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

        String usernameByUser = authentication.getName();
        UserEntity userByUsername = userRepository.findByUsername(usernameByUser).get();

        commentEntity.setUserProfile(userByUsername.getUserProfile());
        commentEntity.setPost(postById);
        commentEntity.setCreatedDate(new Date());
        commentEntity.setModifiedDate(new Date());

        CommentEntity newComment = commentRepository.save(commentEntity);
        return responseComment(newComment);
    }

    @Override
    public CommentDTO updateCommentById(CommentDTO commentDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usernameClient = authentication.getName();

        CommentEntity oldComment = commentRepository.findById(commentDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentDTO.getId()));
        String usernameOwner = oldComment.getUserProfile().getUser().getUsername();
        PostEntity postByComment = oldComment.getPost();

        if (!usernameClient.equals(usernameOwner)) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Comment does not belong to this user");
        }

        UserEntity userByUsername = userRepository.findByUsername(usernameClient).get();

        CommentEntity commentEntity = converter.toEntity(oldComment, commentDTO);
        commentEntity.setUserProfile(userByUsername.getUserProfile());
        commentEntity.setPost(postByComment);
        commentEntity.setCreatedDate(oldComment.getCreatedDate());
        commentEntity.setModifiedDate(new Date());

        CommentEntity newComment = commentRepository.save(commentEntity);
        return responseComment(newComment);
    }

    public CommentDTO getById(Integer id) {
        CommentEntity commentById = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

        return responseComment(commentById);
    }

    @Override
    public List<CommentDTO> getCommentsByPostId(Integer idPost) {
        PostEntity postEntity = postRepository.findById(idPost)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", idPost));

        Set<CommentEntity> commentEntities = postEntity.getComments();

        List<CommentDTO> commentList = commentEntities
                .stream()
                .map(comment -> responseComment(comment))
                .collect(Collectors.toList());

        return commentList;
    }

    @Override
    public void deleteById(Integer id) {
        CommentEntity commentById = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

        commentRepository.delete(commentById);
    }

    private CommentDTO responseComment(CommentEntity savedEntity) {
        CommentDTO commentDTO = converter.toDTO(savedEntity);
        UserProfileDTO profileDTO = commentDTO.getUserProfile();
        resetUrlImageProfile(profileDTO);

        return commentDTO;
    }

    private void resetUrlImageProfile(UserProfileDTO profileDTO) {
        String urlAvatar = urlResponseImageProfile(profileDTO.getAvatarPhoto());
        String urlCover = urlResponseImageProfile((profileDTO.getCoverPhoto()));

        profileDTO.setAvatarPhoto(urlAvatar);
        profileDTO.setCoverPhoto(urlCover);
    }

    private String urlResponseImageProfile(String oldPath) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("files/" + oldPath)
                .toUriString();
    }
}



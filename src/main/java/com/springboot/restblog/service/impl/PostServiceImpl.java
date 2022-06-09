package com.springboot.restblog.service.impl;

import com.springboot.restblog.exception.APIException;
import com.springboot.restblog.exception.ResourceNotFoundException;
import com.springboot.restblog.model.converter.PostConverter;
import com.springboot.restblog.model.entity.CategoryEntity;
import com.springboot.restblog.model.entity.PostEntity;
import com.springboot.restblog.model.payload.CustomUser;
import com.springboot.restblog.model.payload.PostDTO;
import com.springboot.restblog.model.payload.PostResponse;
import com.springboot.restblog.repository.CategoryRepository;
import com.springboot.restblog.repository.PostRepository;
import com.springboot.restblog.repository.UserRepository;
import com.springboot.restblog.service.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements IPostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostConverter converter;

    public PostServiceImpl(PostRepository postRepository, PostConverter converter) {
        this.postRepository = postRepository;
        this.converter = converter;
    }

    @Override
    public PostDTO savePost(Integer userId, Integer[] categoryIds, PostDTO postDTO) {
        PostEntity postEntity;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Integer id = customUser.getUserId();

        if (!id.equals(userId)) {
            throw new APIException(HttpStatus.BAD_REQUEST, "User do not allow access this post");
        }

        //check category list from controller and return new Set<CategoryEntity>
        Set<CategoryEntity> categoryByIds = new HashSet<>();
        for (Integer categoryId : categoryIds) {
            CategoryEntity categoryById = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "name", categoryId));
            categoryByIds.add(categoryById);
        }

        if (postDTO.getId() != null) {  //post is existed
            //update
            PostEntity oldPost = postRepository.findById(postDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postDTO.getId()));

            postEntity = converter.toEntity(postDTO, oldPost);

            if (!postEntity.getUser().getId().equals(userId)) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Post do not belong user");
            }
        } else {
            //create
            postEntity = converter.toEntity(postDTO);
        }

        postEntity.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId)));
        //issue here
        postEntity.setCategoryEntities(categoryByIds);

        PostEntity newPost = postRepository.save(postEntity);
        return converter.toDTO(newPost);
    }

    @Override
    public PostResponse getAll(Integer pageNo, Integer pageSize, String sortBy, String sortDir) {

        Sort sortOj = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sortOj);

        Page<PostEntity> postEntities = postRepository.findAll(pageable);

        List<PostEntity> postEntitiesList = postEntities.getContent();

        List<PostDTO> contentList = postEntitiesList.stream()
                .map(post -> converter.toDTO(post))
                .collect(Collectors.toList());

        PostResponse postResponse = new PostResponse();

        postResponse.setContent(contentList);
        postResponse.setPageNo(postEntities.getNumber());
        postResponse.setPageSize(postEntities.getSize());
        postResponse.setTotalElements((int) postEntities.getTotalElements());
        postResponse.setTotalPages(postEntities.getTotalPages());
        postResponse.setLast(postEntities.isLast());

        return postResponse;
    }

    @Override
    public PostDTO getById(Integer id) {
        PostEntity postEntity = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        return converter.toDTO(postEntity);
    }

    @Override
    public void deleteById(Integer userId, Integer id) {
        PostEntity postResponse = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        if (!postResponse.getUser().getId().equals(userId)) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Post do not belong user");
        }
        postRepository.delete(postResponse);
    }
}

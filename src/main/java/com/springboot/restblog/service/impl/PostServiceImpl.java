package com.springboot.restblog.service.impl;

import com.springboot.restblog.exception.APIException;
import com.springboot.restblog.exception.ResourceNotFoundException;
import com.springboot.restblog.model.converter.PostConverter;
import com.springboot.restblog.model.entity.CategoryEntity;
import com.springboot.restblog.model.entity.PostEntity;
import com.springboot.restblog.model.entity.RoleEntity;
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

    @Override
    public PostDTO savePost(Integer[] categoryIds, PostDTO postDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Integer id = customUser.getUserId();

        PostEntity postEntity = converter.toEntity(postDTO);

        postEntity.setUser(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id)));

        //check category list from controller and return new Set<CategoryEntity>
        Set<CategoryEntity> categoryByIds = new HashSet<>();
        for (Integer categoryId : categoryIds) {
            CategoryEntity categoryById = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "name", categoryId));
            //set list category for a post
            categoryByIds.add(categoryById);
            //call list post from a category
            Set<PostEntity> postEntitiesByOneCategory = categoryById.getPostEntities();
            //set a post to list post of category
            postEntitiesByOneCategory.add(postEntity);
            //re-set post list for a category
            categoryById.setPostEntities(postEntitiesByOneCategory);
        }

        postEntity.setCategoryEntities(categoryByIds);

        PostEntity newPost = postRepository.save(postEntity);
        return converter.toDTO(newPost);
    }

    @Override
    public PostDTO editPost(PostDTO postDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailClient = authentication.getName();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Set<RoleEntity> roles = customUser.getRoles();

        PostEntity oldPost = postRepository.findById(postDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postDTO.getId()));
        String emailOwner = oldPost.getUser().getEmail();

        for (RoleEntity roleEntity : roles) {
            if (!roleEntity.getName().equals("ROLE_ADMIN")) {
                if (!emailClient.equals(emailOwner)) {
                    throw new APIException(HttpStatus.BAD_REQUEST, "User do not allow access this post");
                }
            }
        }

        PostEntity newPost = postRepository.save(converter.toEntity(postDTO, oldPost));
        return converter.toDTO(newPost);
    }

    @Override
    public PostResponse getAll(Integer pageNo, Integer pageSize, String sortBy, String sortDir) {

        Sort sortOj = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sortOj);
        Page<PostEntity> postEntities = postRepository.findAll(pageable);

        PostResponse postResponse = pagingPost(postEntities);

        return postResponse;
    }

    @Override
    public PostResponse getByCategory(Integer categoryId, Integer pageNo,
                                      Integer pageSize, String sortBy, String sortDir) {
        Sort sortOj = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        Pageable pageable = PageRequest.of(pageNo, pageSize, sortOj);
        Page<PostEntity> postEntities = postRepository.findPostEntityByCategoryEntities(categoryEntity, pageable);

        PostResponse postResponse = pagingPost(postEntities);

        return postResponse;
    }

    private PostResponse pagingPost(Page<PostEntity> postEntities) {

        List<PostEntity> postEntityList = postEntities.getContent();

        List<PostDTO> contentList
                = postEntityList.stream().map(post -> converter.toDTO(post))
                .collect(Collectors.toList());

        PostResponse postResponse = new PostResponse();
        postResponse.setPageNo(postEntities.getNumber());
        postResponse.setContent(contentList);
        postResponse.setPageSize(postEntities.getSize());
        postResponse.setTotalPages(postEntities.getTotalPages());
        postResponse.setTotalElements((int) postEntities.getTotalElements());
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
    public void deleteById(Integer id) {
        PostEntity postResponse = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        postRepository.delete(postResponse);
    }
}

package com.springboot.restblog.service.impl;

import com.springboot.restblog.exception.APIException;
import com.springboot.restblog.exception.ResourceNotFoundException;
import com.springboot.restblog.model.converter.PostConverter;
import com.springboot.restblog.model.entity.CategoryEntity;
import com.springboot.restblog.model.entity.PostEntity;
import com.springboot.restblog.model.entity.RoleEntity;
import com.springboot.restblog.model.payload.CustomUser;
import com.springboot.restblog.model.payload.PostDTO;
import com.springboot.restblog.model.payload.PageResponse;
import com.springboot.restblog.repository.CategoryRepository;
import com.springboot.restblog.repository.PostRepository;
import com.springboot.restblog.repository.UserRepository;
import com.springboot.restblog.service.IPostService;
import com.springboot.restblog.utils.FileUploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

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
    public PostDTO savePost(Integer[] categoryIds, PostDTO postDTO, MultipartFile file) throws IOException {

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
        postEntity.setCreated_date(new Date());

        PostEntity savedPost = postRepository.save(postEntity);
        PostDTO responseDto = saveOrUpdateImage(file, postEntity, savedPost);

        return responseDto;
    }

    @Override
    public PostDTO editPost(PostDTO postDTO, MultipartFile file) throws IOException {
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
        PostEntity postEntity = converter.toEntity(postDTO, oldPost);
        postEntity.setModified_date(new Date());
        PostDTO responseDto = saveOrUpdateImage(file, postEntity, oldPost);

        return responseDto;
    }

    @Override
    public PageResponse getAll(Integer pageNo, Integer pageSize, String sortBy, String sortDir) {

        Sort sortOj = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sortOj);
        Page<PostEntity> postEntities = postRepository.findAll(pageable);

        PageResponse pageResponse = pagingPost(postEntities);

        return pageResponse;
    }

    @Override
    public PageResponse getByCategory(Integer categoryId, Integer pageNo,
                                      Integer pageSize, String sortBy, String sortDir) {
        Sort sortOj = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        Pageable pageable = PageRequest.of(pageNo, pageSize, sortOj);
        Page<PostEntity> postEntities = postRepository.findPostEntityByCategoryEntities(categoryEntity, pageable);

        PageResponse pageResponse = pagingPost(postEntities);

        return pageResponse;
    }

    private PageResponse pagingPost(Page<PostEntity> postEntities) {
        List<PostEntity> postEntityList = postEntities.getContent();

        List<PostDTO> contentList = new ArrayList<>();
        for (PostEntity postEntity : postEntityList) {
            PostDTO postDTO = converter.toDTO(postEntity);
            setUrlImage(postDTO, postEntity);
            contentList.add(postDTO);
        }

        PageResponse pageResponse = new PageResponse();
        pageResponse.setPageNo(postEntities.getNumber());
        pageResponse.setContent(contentList);
        pageResponse.setPageSize(postEntities.getSize());
        pageResponse.setTotalPages(postEntities.getTotalPages());
        pageResponse.setTotalElements((int) postEntities.getTotalElements());
        pageResponse.setLast(postEntities.isLast());

        return pageResponse;
    }

    @Override
    public PostDTO getById(Integer id) {
        PostEntity postEntity = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        PostDTO responseDto = converter.toDTO(postEntity);
        setUrlImage(responseDto, postEntity);
        return responseDto;
    }

    @Override
    public void deleteById(Integer id) {
        PostEntity postResponse = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        postRepository.delete(postResponse);
    }

    private PostDTO saveOrUpdateImage(MultipartFile file,PostEntity entity, PostEntity savedPost) throws IOException {
        if (!file.isEmpty()) {
            String uploadDir = "uploaded-images/post_thumbnails/" + savedPost.getId();
            float fileSizeMegabytes = file.getSize() / 1000000.0f;
            if (fileSizeMegabytes > 5.0f) {
                throw new RuntimeException("File must be maximum 5 megabytes");
            }
            FileUploadUtils.cleanDir(uploadDir);
            Path path = FileUploadUtils.saveFile(uploadDir, file);
            savedPost.setThumbnails(path.toString().replace("\\", "/"));
        } else {
            if (savedPost.getThumbnails() == null) {
                savedPost.setThumbnails(null);
            }
        }

        PostEntity newPost = postRepository.save(entity);
        PostDTO responseDto = converter.toDTO(newPost);

        setUrlImage(responseDto, newPost);
        return responseDto;
    }

    private void setUrlImage(PostDTO responseDto, PostEntity savedPost) {
        String urlImage = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("files/" + savedPost.getThumbnails())
                .toUriString();
        responseDto.setThumbnails(urlImage);
    }
}

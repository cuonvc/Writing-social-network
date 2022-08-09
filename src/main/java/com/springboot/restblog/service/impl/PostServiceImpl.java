package com.springboot.restblog.service.impl;

import com.springboot.restblog.exception.APIException;
import com.springboot.restblog.exception.ResourceNotFoundException;
import com.springboot.restblog.model.converter.PostConverter;
import com.springboot.restblog.model.entity.*;
import com.springboot.restblog.model.payload.*;
import com.springboot.restblog.repository.CategoryRepository;
import com.springboot.restblog.repository.PostRepository;
import com.springboot.restblog.repository.UserRepository;
import com.springboot.restblog.service.IPostService;
import com.springboot.restblog.utils.FileUploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
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
    public PostDTO savePost(Integer[] categoryIds, PostDTO postDTO, MultipartFile file) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Integer id = customUser.getUserId();

        PostEntity postEntity = converter.toEntity(postDTO);

        UserEntity userById = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        postEntity.setUserProfile(userById.getUserProfile());

        //check category list from controller and return new Set<CategoryEntity>
        Set<CategoryEntity> categoryByIds = new HashSet<>();
        for (Integer categoryId : categoryIds) {
            CategoryEntity categoryById = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "name", categoryId));
            //set list category for a post
            categoryByIds.add(categoryById);
            //call list post from a category
            Set<PostEntity> postEntitiesByOneCategory = categoryById.getPosts();
            //set a post to list post of category
            postEntitiesByOneCategory.add(postEntity);
            //re-set post list for a category
            categoryById.setPosts(postEntitiesByOneCategory);
        }

        postEntity.setCategories(categoryByIds);
        postEntity.setCreatedDate(new Date());
        postEntity.setModifiedDate(new Date());

        PostEntity savedPost = postRepository.save(postEntity);

        PostDTO responseDto = responsePost(file, postEntity, savedPost);
        return responseDto;
    }

    @Override
    public PostDTO editPost(PostDTO postDTO, MultipartFile file) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usernamClient = authentication.getName();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Set<RoleEntity> roles = customUser.getRoles();

        PostEntity oldPost = postRepository.findById(postDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postDTO.getId()));
        String usernameOwner = oldPost.getUserProfile().getUser().getUsername();


        for (RoleEntity roleEntity : roles) {
            if (!roleEntity.getName().equals("ROLE_ADMIN")) {
                if (!usernamClient.equals(usernameOwner)) {
                    throw new APIException(HttpStatus.BAD_REQUEST, "User do not allow access this post");
                }
            }
        }
        PostEntity postEntity = converter.toEntity(postDTO, oldPost);
        postEntity.setModifiedDate(new Date());

        PostDTO responseDto = responsePost(file, postEntity, oldPost);
        return responseDto;
    }

    @Override
    public PageResponsePost getAll(Integer pageNo, Integer pageSize, String sortBy, String sortDir) {

        Sort sortOj = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sortOj);
        Page<PostEntity> postEntities = postRepository.findAll(pageable);

        PageResponsePost pageResponsePost = pagingPost(postEntities);

        return pageResponsePost;
    }

    @Override
    public PageResponsePost getByCategory(Integer categoryId, Integer pageNo,
                                          Integer pageSize, String sortBy, String sortDir) {
        Sort sortOj = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        Pageable pageable = PageRequest.of(pageNo, pageSize, sortOj);
        Page<PostEntity> postEntities = postRepository.findPostEntitiesByCategories(categoryEntity, pageable);

        PageResponsePost pageResponsePost = pagingPost(postEntities);

        return pageResponsePost;
    }

    @Override
    public PageResponsePost filterByKeyword(String keyword, Integer pageNo, Integer pageSize, String sortBy, String sortDir) {
        String newStr = keyword.trim()
                .replaceAll("[ ]+", " "); //multiple spaces to single space
        String[] words = newStr.split(" ");
        List<PostEntity> listResponseEntity = new ArrayList<>();

        for (String key : words) {
            List<PostEntity> postEntityList = postRepository.searchPosts(key);

            for (PostEntity postEntity : postEntityList) {
                if (listResponseEntity.isEmpty()) {
                    listResponseEntity.add(postEntity);
                } else {
                    //error (ConcurrentModificationException) when using forEach
                    int count = 0;
                    for (int i = 0; i < listResponseEntity.size(); i++) {
                        if (!postEntity.getId().equals(listResponseEntity.get(i).getId())) {
                            count++;
                        }
                    }
                    for (int i = 0; i < listResponseEntity.size(); i++) {
                        if (count == listResponseEntity.size()
                                && !postEntity.getId().equals(listResponseEntity.get(i).getId())) {
                            listResponseEntity.add(postEntity);
                        }
                    }
                }
            }
        }

        Sort sortObj = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sortObj);
        Page<PostEntity> page =
                new PageImpl<>(listResponseEntity, pageable, listResponseEntity.size());
        PageResponsePost pageResponse = pagingPost(page);
        return pageResponse;
    }

    private PageResponsePost pagingPost(Page<PostEntity> postEntities) {
        List<PostEntity> postEntityList = postEntities.getContent();

        List<PostDTO> contentList = new ArrayList<>();
        for (PostEntity postEntity : postEntityList) {
            PostDTO postDTO = converter.toDTO(postEntity);
            setUrlImage(postDTO, postEntity);

            UserProfileDTO profileDTO = postDTO.getUserProfile();
            resetUrlImageProfile(profileDTO);

            contentList.add(postDTO);
        }

        PageResponsePost pageResponsePost = new PageResponsePost();
        pageResponsePost.setPageNo(postEntities.getNumber());
        pageResponsePost.setContent(contentList);
        pageResponsePost.setPageSize(postEntities.getSize());
        pageResponsePost.setTotalPages(postEntities.getTotalPages());
        pageResponsePost.setTotalElements((int) postEntities.getTotalElements());
        pageResponsePost.setLast(postEntities.isLast());

        return pageResponsePost;
    }

    @Override
    public PostDTO getById(Integer id) {
        PostEntity postEntity = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        PostDTO responseDto = converter.toDTO(postEntity);
        setUrlImage(responseDto, postEntity);

        UserProfileDTO profileDTO = responseDto.getUserProfile();
        resetUrlImageProfile(profileDTO);

        int size = responseDto.getComments().size();
        List<CommentDTO> commentDTOList = new ArrayList<>(size);
        commentDTOList.addAll(responseDto.getComments());
        for (CommentDTO comment : commentDTOList) {
            resetUrlImageProfile(comment.getUserProfile());
        }

        return responseDto;
    }

    private PostDTO responsePost(MultipartFile file, PostEntity entity, PostEntity savedEntity)
            throws IOException {
        PostDTO postDTO = saveOrUpdateImage(file, entity, savedEntity);
        UserProfileDTO profileDTO = postDTO.getUserProfile();
        resetUrlImageProfile(profileDTO);

        return postDTO;
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
                savedPost.setThumbnails("uploaded-images/post_thumbnails/default/default-thumbnail.jpg");
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

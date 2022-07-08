package com.springboot.restblog.controller;

import com.springboot.restblog.anotation.ValidImage;
import com.springboot.restblog.model.payload.PostDTO;
import com.springboot.restblog.model.payload.PageResponsePost;
import com.springboot.restblog.service.IPostService;
import com.springboot.restblog.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
public class PostController {

    //Spring recommends using the interface to maintain loose coupling between the dependencies
    @Autowired
    private IPostService postService;

    @PostMapping("/category/{categoryIds}/post")
    //mix json and file trong formData
    public ResponseEntity<PostDTO> createPost(@Valid @PathVariable(name = "categoryIds") Integer[] categoryIds,
                                              @RequestPart ("textField") PostDTO postDTO,
                                              @RequestPart ("image") @ValidImage MultipartFile file) throws IOException {

        PostDTO postResponse = postService.savePost(categoryIds, postDTO, file);
        return new ResponseEntity<>(postResponse, HttpStatus.CREATED);
    }
//có thể sử dụng thuần formData (set thumbnails có dataType là MultipartFile)
//    public ResponseEntity<PostDTO> createPost(@Valid @PathVariable(name = "categoryIds") Integer[] categoryIds,
//                                              @ModelAttribute PostDTO postDTO) throws IOException {
//
//        PostDTO postResponse = postService.savePost(categoryIds, postDTO);
//        return new ResponseEntity<>(postResponse, HttpStatus.CREATED);
//    }

    @GetMapping("/posts")
    public PageResponsePost getAllPost(@RequestParam(value = "pageNo",
                                       defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNo,
                                       @RequestParam(value = "pageSize",
                                       defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                       @RequestParam(value = "sortBy",
                                       defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
                                       @RequestParam(value = "sortDir",
                                       defaultValue = AppConstants.SORT_DIRECTION) String sortDir) {

        return postService.getAll(pageNo, pageSize, sortBy, sortDir);
    }

    @GetMapping("/category/{categoryId}/posts")
    public PageResponsePost getAllByCategory(@PathVariable(name = "categoryId") Integer categoryId,
                                             @RequestParam(value = "pageNo",
                                            defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNo,
                                             @RequestParam(value = "pageSize",
                                            defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                             @RequestParam(value = "sortBy",
                                            defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
                                             @RequestParam(value = "sortDir",
                                            defaultValue = AppConstants.SORT_DIRECTION) String sortDir) {
        return postService.getByCategory(categoryId, pageNo, pageSize, sortBy, sortDir);
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable(name = "id") Integer id) {
        return ResponseEntity.ok(postService.getById(id));
    }

    //- ResponseEntity<>(x, HttpStatus.OK) - You can use this method to pass data in the
    // method body along with a any status code (any status code 200, 201, 204).
    //- ResponseEntity.ok(x) - use this method to pass data in the method body with
    // only status code 200 (OK).

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PutMapping("/post/{id}")
    public ResponseEntity<PostDTO> updatePost(@Valid @PathVariable(name = "id") Integer id,
                                              @RequestPart ("textField") PostDTO postDTO,
                                              @RequestPart ("image") @ValidImage MultipartFile file) throws IOException {
        postDTO.setId(id);
        PostDTO postResponse = postService.editPost(postDTO, file);
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/post/{id}")
    public ResponseEntity<String> deletePost(@PathVariable(name = "id") Integer id) {
        postService.deleteById(id);
        return new ResponseEntity<>("Post deleted successfully", HttpStatus.OK);
    }
}

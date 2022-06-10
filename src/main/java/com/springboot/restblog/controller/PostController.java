package com.springboot.restblog.controller;

import com.springboot.restblog.exception.APIException;
import com.springboot.restblog.model.payload.CustomUser;
import com.springboot.restblog.model.payload.PostDTO;
import com.springboot.restblog.model.payload.PostResponse;
import com.springboot.restblog.service.IPostService;
import com.springboot.restblog.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PostController {

    //Spring recommends using the interface to maintain loose coupling between the dependencies
    @Autowired
    private IPostService iPostService;

    public PostController(IPostService iPostService) {
        this.iPostService = iPostService;
    }

    @PostMapping("/user/{userId}/posts/category/{categoryIds}")
    public ResponseEntity<PostDTO> createPost(@Valid @PathVariable(name = "userId") Integer userId,
                                                         @PathVariable(name = "categoryIds") Integer[] categoryIds,
                                                         @RequestBody PostDTO postDTO) {
        PostDTO postResponse = iPostService.savePost(userId, categoryIds, postDTO);
        return new ResponseEntity<>(postResponse, HttpStatus.CREATED);
    }

    @GetMapping("/posts")
    public PostResponse getAllPost(@RequestParam(value = "pageNo",
                                       defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNo,
                                   @RequestParam(value = "pageSize",
                                       defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                   @RequestParam(value = "sortBy",
                                       defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
                                   @RequestParam(value = "sortDir",
                                       defaultValue = AppConstants.SORT_DIRECTION) String sortDir) {

        return iPostService.getAll(pageNo, pageSize, sortBy, sortDir);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable(name = "id") Integer id) {
        return ResponseEntity.ok(iPostService.getById(id));
    }

    //- ResponseEntity<>(x, HttpStatus.OK) - You can use this method to pass data in the
    // method body along with a any status code (any status code 200, 201, 204).
    //- ResponseEntity.ok(x) - use this method to pass data in the method body with
    // only status code 200 (OK).

    @PutMapping("/user/{userId}/posts/{id}")
    public ResponseEntity<PostDTO> updatePost(@Valid @PathVariable(name = "userId") Integer userId,
                                                         @PathVariable(name = "id") Integer id,
                                              @RequestBody PostDTO postDTO) {
        postDTO.setId(id);
        PostDTO postResponse = iPostService.editPost(userId, postDTO);
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<String> deletePost(@PathVariable(name = "id") Integer id) {
        iPostService.deleteById(id);
        return new ResponseEntity<>("Post deleted successfully", HttpStatus.OK);
    }
}

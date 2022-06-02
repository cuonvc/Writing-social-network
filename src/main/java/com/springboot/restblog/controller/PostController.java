package com.springboot.restblog.controller;

import com.springboot.restblog.model.payload.PostDTO;
import com.springboot.restblog.model.payload.PostResponse;
import com.springboot.restblog.service.IPostService;
import com.springboot.restblog.service.impl.PostServiceImpl;
import com.springboot.restblog.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    //Spring recommends using the interface to maintain loose coupling between the dependencies
    @Autowired
    private IPostService iPostService;

    public PostController(IPostService iPostService) {
        this.iPostService = iPostService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<PostDTO> createPost(@Valid @RequestBody PostDTO postDTO) {
        PostDTO postResponse = iPostService.savePost(postDTO);
        return new ResponseEntity<>(postResponse, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
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

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable(name = "id") Integer id) {
        return ResponseEntity.ok(iPostService.getById(id));
    }

    //- ResponseEntity<>(x, HttpStatus.OK) - You can use this method to pass data in the
    // method body along with a any status code (any status code 200, 201, 204).
    //- ResponseEntity.ok(x) - use this method to pass data in the method body with
    // only status code 200 (OK).

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(@Valid @PathVariable(name = "id") Integer id,
                                              @RequestBody PostDTO postDTO) {
        postDTO.setId(id);

        PostDTO postResponse = iPostService.savePost(postDTO);
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable(name = "id") Integer id) {
        iPostService.deleteById(id);
        return new ResponseEntity<>("Post deleted successfully", HttpStatus.OK);
    }
}

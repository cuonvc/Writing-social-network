package com.springboot.restblog.controller;

import com.springboot.restblog.model.payload.PostDTO;
import com.springboot.restblog.service.IPostService;
import com.springboot.restblog.service.impl.PostServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    //Spring recommends using the interface to maintain loose coupling between the dependencies
    @Autowired
    private IPostService iPostService;

    public PostController(IPostService iPostService) {
        this.iPostService = iPostService;
    }

    @PostMapping
    public ResponseEntity<PostDTO> createPost(@RequestBody PostDTO postDTO) {
        PostDTO postResponse = iPostService.savePost(postDTO);
        return new ResponseEntity<>(postResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public List<PostDTO> getAllPost() {
        return iPostService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable(name = "id") Integer id) {
        return ResponseEntity.ok(iPostService.getById(id));
    }
}

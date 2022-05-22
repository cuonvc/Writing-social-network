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

    //default start pageNo = 0 (start with number record 1)
    @GetMapping
    public List<PostDTO> getAllPost(@RequestParam(value = "pageNo",
                                        defaultValue = "1", required = false) Integer pageNo,
                                    @RequestParam(value = "pageSize",
                                        defaultValue = "5", required = false) Integer pageSize) {

        return iPostService.getAll(pageNo - 1, pageSize);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable(name = "id") Integer id) {
        return ResponseEntity.ok(iPostService.getById(id));
    }

    //- ResponseEntity<>(x, HttpStatus.OK) - You can use this method to pass data in the
    // method body along with a any status code (any status code 200, 201, 204).
    //- ResponseEntity.ok(x) - use this method to pass data in the method body with
    // only status code 200 (OK).

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable(name = "id") Integer id,
                                              @RequestBody PostDTO postDTO) {
        postDTO.setId(id);

        PostDTO postResponse = iPostService.savePost(postDTO);
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable(name = "id") Integer id) {
        iPostService.deleteById(id);
        return new ResponseEntity<>("Post deleted successfully", HttpStatus.OK);
    }
}

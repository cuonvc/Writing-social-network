package com.springboot.restblog.controller;

import com.springboot.restblog.model.payload.CommentDTO;
import com.springboot.restblog.service.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CommentController {

    @Autowired
    private ICommentService commentService;

    @PostMapping("/post/{postId}/comment")
    public ResponseEntity<CommentDTO> createComment(@PathVariable(name = "postId") Integer postId,
                                                    @Valid @RequestBody CommentDTO commentDTO) {
        CommentDTO commentResponse = commentService.saveComment(postId, commentDTO);

        return new ResponseEntity<>(commentResponse, HttpStatus.CREATED);
    }

    @PutMapping("/comment/{id}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable(name = "id") Integer id,
                                                    @Valid @RequestBody CommentDTO commentDTO) {
        commentDTO.setId(id);
        CommentDTO commentUpdate = commentService.updateCommentById(commentDTO);

        return new ResponseEntity<>(commentUpdate, HttpStatus.OK);
    }

    @GetMapping("/comment/{id}")
    public ResponseEntity<CommentDTO> getCommentByid(@PathVariable(name = "id") Integer id) {
        CommentDTO commentResponse = commentService.getById(id);
        return new ResponseEntity<>(commentResponse, HttpStatus.OK);
    }

    @GetMapping("/post/{postId}/comments")
    public List<CommentDTO> listCommentsByPostId(@PathVariable(name = "postId") Integer idPost) {
        List<CommentDTO> listResponse = commentService.getCommentsByPostId(idPost);
        return listResponse;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/comment/{id}")
    public HttpStatus deleteCommentById(@PathVariable(name = "id") Integer id) {
        commentService.deleteById(id);
        return HttpStatus.OK;
    }
}

package com.example.blog_api.controller;

import com.example.blog_api.dto.Comment;
import com.example.blog_api.dto.CommentCreateRequest;
import com.example.blog_api.security.AuthenticationHelper;
import com.example.blog_api.service.CommentService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {
    private final CommentService commentService;
    private final AuthenticationHelper authHelper;

    public CommentController(CommentService commentService, AuthenticationHelper authHelper) {
        this.commentService = commentService;
        this.authHelper = authHelper;
    }

    @GetMapping("/blogs/{blogId}/comments")
    public ResponseEntity<List<Comment>> getCommentsByBlog(@PathVariable String blogId) {
        List<Comment> comments = commentService.searchCommentByBlogId(blogId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/blogs/{blogId}/comments")
    public ResponseEntity<Comment> createComment(
            @PathVariable String blogId,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CommentCreateRequest request) {
        // トークンからuserIdを取得
        String userId = authHelper.getCurrentUserId(authHeader);
        
        Comment comment = commentService.createComment(blogId, userId, request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @GetMapping("/comments/{commentId}")
    public ResponseEntity<Comment> getComment(@PathVariable String commentId) {
        Comment comment = commentService.getCommentById(commentId);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable String commentId,
            @RequestHeader("Authorization") String authHeader) {
        // 自分のコメントかチェック
        Comment comment = commentService.getCommentById(commentId);
        if(!authHelper.getCurrentUserId(authHeader).equals(comment.getUserId())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}

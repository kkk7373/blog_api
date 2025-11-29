package com.example.blog_api.controller;

import com.example.blog_api.dto.BlogLike;
import com.example.blog_api.dto.BlogLikeCreateRequest;
import com.example.blog_api.dto.CommentLike;
import com.example.blog_api.dto.CommentLikeCreateRequest;
import com.example.blog_api.security.AuthenticationHelper;
import com.example.blog_api.service.BlogLikeService;
import com.example.blog_api.service.CommentLikeService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LikeController {
    private final AuthenticationHelper authHelper;
    private final BlogLikeService blogLikeService;
    private final CommentLikeService commentLikeService;

    public LikeController(AuthenticationHelper authHelper, BlogLikeService blogLikeService, CommentLikeService commentLikeService) {
        this.authHelper = authHelper;
        this.blogLikeService = blogLikeService;
        this.commentLikeService = commentLikeService;
    }
    // ========== Blog Like ==========
    
    @GetMapping("/blogs/{blogId}/likes")
    public ResponseEntity<List<BlogLike>> getBlogLikes(@PathVariable String blogId) {
        
        return ResponseEntity.ok(blogLikeService.getLikesByBlogId(blogId));
    }

    @PostMapping("/blogs/{blogId}/likes")
    public ResponseEntity<BlogLike> createBlogLike(
            @PathVariable String blogId,
            @RequestBody BlogLikeCreateRequest request,
            @RequestHeader("Authorization") String authHeader) {
                      
        String userId = authHelper.getCurrentUserId(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        try {
            BlogLike createdLike = blogLikeService.createBlogLike(blogId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLike);
        } catch (IllegalStateException e) {
            // 既にいいね済み
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/blogs/{blogId}/likes")
    public ResponseEntity<Void> deleteBlogLike(
            @PathVariable String blogId,
            @RequestHeader("Authorization") String authHeader) {
        
        String userId = authHelper.getCurrentUserId(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        blogLikeService.deleteBlogLike(blogId, userId);
        return ResponseEntity.noContent().build();
    }

    // ========== Comment Like ==========
    
    @GetMapping("/comments/{commentId}/likes")
    public ResponseEntity<List<CommentLike>> getCommentLikes(@PathVariable String commentId) {
        return ResponseEntity.ok(commentLikeService.getLikesByCommentId(commentId));
    }

    @PostMapping("/comments/{commentId}/likes")
    public ResponseEntity<CommentLike> createCommentLike(
            @PathVariable String commentId,
            @RequestBody CommentLikeCreateRequest request,
            @RequestHeader("Authorization") String authHeader) {
        
        String userId = authHelper.getCurrentUserId(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        try {
            CommentLike createdLike = commentLikeService.createCommentLike(commentId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLike);
        } catch (IllegalStateException e) {
            // 既にいいね済み
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/comments/{commentId}/likes")
    public ResponseEntity<Void> deleteCommentLike(
            @PathVariable String commentId,
            @RequestHeader("Authorization") String authHeader) {
        
        String userId = authHelper.getCurrentUserId(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        commentLikeService.deleteCommentLike(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}

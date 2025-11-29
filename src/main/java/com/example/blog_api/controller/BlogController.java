package com.example.blog_api.controller;

import com.example.blog_api.dto.Blog;
import com.example.blog_api.dto.BlogCreateRequest;
import com.example.blog_api.dto.BlogUpdateRequest;
import com.example.blog_api.dto.Tag;
import com.example.blog_api.security.AuthenticationHelper;
import com.example.blog_api.service.BlogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blogs")
public class BlogController {

    private final AuthenticationHelper authHelper;
    private final BlogService blogService;

    public BlogController(AuthenticationHelper authHelper, BlogService blogService) {
        this.authHelper = authHelper;
        this.blogService = blogService;
    }

    @GetMapping
    public ResponseEntity<List<Blog>> getAllBlogs() {
        List<Blog> blogs = blogService.getAllBlogs();
        return ResponseEntity.ok(blogs);
    }

    @PostMapping
    public ResponseEntity<Blog> createBlog(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody BlogCreateRequest request) {
        String userId = authHelper.getCurrentUserId(authHeader);
        
        Blog blog = blogService.createBlog(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(blog);
    }

    @GetMapping("/{blogId}")
    public ResponseEntity<Blog> getBlog(@PathVariable String blogId) {
        Blog blog = blogService.getBlogById(blogId);
        return ResponseEntity.ok(blog);
    }

    @GetMapping("/{blogId}/tags")
    public ResponseEntity<List<Tag>> getBlogTags(@PathVariable String blogId) {
        List<Tag> tags = blogService.getTagsForBlog(blogId);
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Blog>> searchBlogsByTags(@RequestParam List<String> tags) {
        List<Blog> blogs = blogService.searchBlogsByTags(tags);
        return ResponseEntity.ok(blogs);
    }

    @PutMapping("/{blogId}")
    public ResponseEntity<Blog> updateBlog(
            @PathVariable String blogId,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody BlogUpdateRequest request) {
        if(!authHelper.getCurrentUserId(authHeader).equals(blogService.getBlogById(blogId).getUserId())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Blog blog = blogService.updateBlog(blogId, request);
        return ResponseEntity.ok(blog);
    }

    @DeleteMapping("/{blogId}")
    public ResponseEntity<Void> deleteBlog(
            @PathVariable String blogId,
            @RequestHeader("Authorization") String authHeader) {
                if(!authHelper.getCurrentUserId(authHeader).equals(blogService.getBlogById(blogId).getUserId())){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }

        blogService.deleteBlog(blogId);
        return ResponseEntity.noContent().build();
    }
}

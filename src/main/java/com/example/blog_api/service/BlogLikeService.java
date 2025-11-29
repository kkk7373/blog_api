package com.example.blog_api.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.blog_api.dto.BlogLike;
import com.example.blog_api.exception.DuplicateResourceException;
import com.example.blog_api.repository.BlogLikeRepository;

@Service
public class BlogLikeService {
    private final BlogLikeRepository blogLikeRepository;

    public BlogLikeService(BlogLikeRepository blogLikeRepository) {
        this.blogLikeRepository = blogLikeRepository;
    }

    public List<BlogLike> getLikesByBlogId(String blogId) {
        return blogLikeRepository.findByBlogId(blogId);
    }
    
    public BlogLike createBlogLike(String blogId, String userId) {
        // 既にいいね済みかチェック
        if (blogLikeRepository.findByBlogIdAndUserId(blogId, userId).isPresent()) {
            throw new DuplicateResourceException("既にこのブログにいいね済みです");
        }
        
        BlogLike blogLike = new BlogLike();
        blogLike.setBlogId(blogId);
        blogLike.setUserId(userId);
        blogLike.setCreatedAt(LocalDateTime.now());
        return blogLikeRepository.save(blogLike);
    }
    
    @Transactional
    public void deleteBlogLike(String blogId, String userId) {
        blogLikeRepository.deleteByBlogIdAndUserId(blogId, userId);
    }
}
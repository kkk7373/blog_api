package com.example.blog_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.blog_api.dto.BlogLike;

@Repository
public interface BlogLikeRepository extends JpaRepository<BlogLike, String> {
    List<BlogLike> findByBlogId(String blogId);
    Optional<BlogLike> findByBlogIdAndUserId(String blogId, String userId);
    void deleteByBlogIdAndUserId(String blogId, String userId);
}

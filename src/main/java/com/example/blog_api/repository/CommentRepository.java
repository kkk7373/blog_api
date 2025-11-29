package com.example.blog_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.blog_api.dto.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String>{
    List<Comment> findByBlogId(String blogId);
}

package com.example.blog_api.repository;

import com.example.blog_api.dto.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, String> {
    List<Blog> findByUserId(String userId);
    List<Blog> findByIdIn(List<String> ids);
}

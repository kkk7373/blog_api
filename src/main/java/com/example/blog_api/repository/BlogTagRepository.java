package com.example.blog_api.repository;

import com.example.blog_api.dto.BlogTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogTagRepository extends JpaRepository<BlogTag, String> {
    List<BlogTag> findByBlogId(String blogId);
    List<BlogTag> findByTagId(String tagId);
    
    @Query("SELECT bt.blogId FROM BlogTag bt WHERE bt.tagId IN :tagIds GROUP BY bt.blogId")
    List<String> findBlogIdsByTagIds(@Param("tagIds") List<String> tagIds);
}

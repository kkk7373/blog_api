package com.example.blog_api.controller;

import com.example.blog_api.dto.Blog;
import com.example.blog_api.dto.BlogCreateRequest;
import com.example.blog_api.security.AuthenticationHelper;
import com.example.blog_api.service.BlogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("BlogController 統合テスト")
class BlogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BlogService blogService;

    @MockBean
    private AuthenticationHelper authHelper;

    @Test
    @DisplayName("全ブログ取得 - 認証なし")
    void getAllBlogs_NoAuth() throws Exception {
        // Given
        Blog blog1 = new Blog();
        blog1.setId("blog1");
        blog1.setContent("Content 1");

        Blog blog2 = new Blog();
        blog2.setId("blog2");
        blog2.setContent("Content 2");

        List<Blog> blogs = Arrays.asList(blog1, blog2);
        when(blogService.getAllBlogs()).thenReturn(blogs);

        // When & Then
        mockMvc.perform(get("/blogs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("blog1"))
                .andExpect(jsonPath("$[1].id").value("blog2"));
    }

    @Test
    @DisplayName("ブログ取得 - 認証なし")
    void getBlog_NoAuth() throws Exception {
        // Given
        Blog blog = new Blog();
        blog.setId("blog123");
        blog.setContent("Test content");
        blog.setUserId("user123");

        when(blogService.getBlogById("blog123")).thenReturn(blog);

        // When & Then
        mockMvc.perform(get("/blogs/blog123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("blog123"))
                .andExpect(jsonPath("$.content").value("Test content"))
                .andExpect(jsonPath("$.userId").value("user123"));
    }

    @Test
    @WithMockUser
    @DisplayName("ブログ作成 - 成功")
    void createBlog_Success() throws Exception {
        // Given
        BlogCreateRequest request = new BlogCreateRequest();
        request.setContent("New blog content");

        Blog createdBlog = new Blog();
        createdBlog.setId("blog123");
        createdBlog.setContent("New blog content");
        createdBlog.setUserId("user123");

        when(authHelper.getCurrentUserId(anyString())).thenReturn("user123");
        when(blogService.createBlog(anyString(), any(BlogCreateRequest.class))).thenReturn(createdBlog);

        // When & Then
        mockMvc.perform(post("/blogs")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("blog123"))
                .andExpect(jsonPath("$.content").value("New blog content"));
    }

    @Test
    @DisplayName("ブログ作成 - 認証なし")
    void createBlog_NoAuth() throws Exception {
        // Given
        BlogCreateRequest request = new BlogCreateRequest();
        request.setContent("New blog content");

        // When & Then
        mockMvc.perform(post("/blogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    @DisplayName("ブログ作成 - バリデーションエラー（コンテンツなし）")
    void createBlog_ValidationError_NoContent() throws Exception {
        // Given
        BlogCreateRequest request = new BlogCreateRequest();

        // When & Then
        mockMvc.perform(post("/blogs")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value("content: コンテンツは必須です"));
    }
}

package com.example.blog_api.service;

import com.example.blog_api.dto.Blog;
import com.example.blog_api.dto.BlogCreateRequest;
import com.example.blog_api.exception.ResourceNotFoundException;
import com.example.blog_api.repository.BlogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BlogService 単体テスト")
class BlogServiceTest {

    @Mock
    private BlogRepository blogRepository;

    @Mock
    private TagGenerationService tagGenerationService;

    @Mock
    private TagService tagService;

    @InjectMocks
    private BlogService blogService;

    private Blog testBlog;
    private BlogCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        testBlog = new Blog();
        testBlog.setId("blog123");
        testBlog.setUserId("user123");
        testBlog.setContent("Test blog content");

        createRequest = new BlogCreateRequest();
        createRequest.setContent("Test blog content");
    }

    @Test
    @DisplayName("ブログ作成 - 成功")
    void createBlog_Success() {
        // Given
        List<String> generatedTags = Arrays.asList("Java", "Spring Boot", "テスト");
        when(blogRepository.save(any(Blog.class))).thenReturn(testBlog);
        when(tagGenerationService.generateTags(anyString())).thenReturn(generatedTags);
        doNothing().when(tagService).associateTagsWithBlog(anyString(), anyList());

        // When
        Blog result = blogService.createBlog("user123", createRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("blog123");
        assertThat(result.getContent()).isEqualTo("Test blog content");
        verify(blogRepository).save(any(Blog.class));
        verify(tagGenerationService).generateTags("Test blog content");
        verify(tagService).associateTagsWithBlog("blog123", generatedTags);
    }

    @Test
    @DisplayName("ブログ取得 - 成功")
    void getBlogById_Success() {
        // Given
        when(blogRepository.findById("blog123")).thenReturn(Optional.of(testBlog));

        // When
        Blog result = blogService.getBlogById("blog123");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("blog123");
        verify(blogRepository).findById("blog123");
    }

    @Test
    @DisplayName("ブログ取得 - 見つからない")
    void getBlogById_NotFound() {
        // Given
        when(blogRepository.findById("unknown")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> blogService.getBlogById("unknown"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Blog not found");

        verify(blogRepository).findById("unknown");
    }

    @Test
    @DisplayName("全ブログ取得 - 成功")
    void getAllBlogs_Success() {
        // Given
        Blog blog1 = new Blog();
        blog1.setId("blog1");
        Blog blog2 = new Blog();
        blog2.setId("blog2");
        List<Blog> blogs = Arrays.asList(blog1, blog2);

        when(blogRepository.findAll()).thenReturn(blogs);

        // When
        List<Blog> result = blogService.getAllBlogs();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo("blog1");
        assertThat(result.get(1).getId()).isEqualTo("blog2");
        verify(blogRepository).findAll();
    }

    @Test
    @DisplayName("ユーザーのブログ取得 - 成功")
    void getBlogsByUserId_Success() {
        // Given
        List<Blog> userBlogs = Arrays.asList(testBlog);
        when(blogRepository.findByUserId("user123")).thenReturn(userBlogs);

        // When
        List<Blog> result = blogService.getBlogsByUserId("user123");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo("user123");
        verify(blogRepository).findByUserId("user123");
    }

    @Test
    @DisplayName("ブログ削除 - 成功")
    void deleteBlog_Success() {
        // Given
        when(blogRepository.findById("blog123")).thenReturn(Optional.of(testBlog));
        doNothing().when(blogRepository).delete(any(Blog.class));

        // When
        blogService.deleteBlog("blog123");

        // Then
        verify(blogRepository).findById("blog123");
        verify(blogRepository).delete(testBlog);
    }
}

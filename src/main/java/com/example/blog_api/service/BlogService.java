package com.example.blog_api.service;

import com.example.blog_api.dto.Blog;
import com.example.blog_api.dto.BlogCreateRequest;
import com.example.blog_api.dto.BlogUpdateRequest;
import com.example.blog_api.dto.Tag;
import com.example.blog_api.exception.ResourceNotFoundException;
import com.example.blog_api.repository.BlogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class BlogService {

    private final BlogRepository blogRepository;
    private final TagGenerationService tagGenerationService;
    private final TagService tagService;

    public BlogService(BlogRepository blogRepository, 
                      TagGenerationService tagGenerationService,
                      TagService tagService) {
        this.blogRepository = blogRepository;
        this.tagGenerationService = tagGenerationService;
        this.tagService = tagService;
    }

    /**
     * ブログを作成し、タグを自動生成
     */
    public Blog createBlog(String userId, BlogCreateRequest request) {
        Blog blog = new Blog();
        blog.setUserId(userId);
        blog.setContent(request.getContent());
        blog.setCreatedAt(LocalDateTime.now());
        blog.setUpdatedAt(LocalDateTime.now());

        // ブログを保存
        Blog savedBlog = blogRepository.save(blog);

        // Gemini APIでタグを自動生成
        List<String> generatedTags = tagGenerationService.generateTags(request.getContent());

        // タグをブログに紐付け
        tagService.associateTagsWithBlog(savedBlog.getId(), generatedTags);

        return savedBlog;
    }

    /**
     * ブログを更新し、タグを再生成
     */
    public Blog updateBlog(String blogId, BlogUpdateRequest request) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found"));

        if (request.getContent() != null) {
            blog.setContent(request.getContent());
        }
        blog.setUpdatedAt(LocalDateTime.now());

        Blog updatedBlog = blogRepository.save(blog);

        // 内容が更新された場合、タグを再生成
        if (request.getContent() != null) {
            List<String> generatedTags = tagGenerationService.generateTags(updatedBlog.getContent());
            tagService.associateTagsWithBlog(updatedBlog.getId(), generatedTags);
        }

        return updatedBlog;
    }

    /**
     * ブログを取得
     */
    @Transactional(readOnly = true)
    public Blog getBlogById(String blogId) {
        return blogRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found"));
    }

    /**
     * 全ブログを取得
     */
    @Transactional(readOnly = true)
    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }

    /**
     * ユーザーのブログを取得
     */
    @Transactional(readOnly = true)
    public List<Blog> getBlogsByUserId(String userId) {
        return blogRepository.findByUserId(userId);
    }

    /**
     * タグでブログを検索
     */
    @Transactional(readOnly = true)
    public List<Blog> searchBlogsByTags(List<String> tagNames) {
        List<String> blogIds = tagService.searchBlogIdsByTagNames(tagNames);
        return blogRepository.findByIdIn(blogIds);
    }

    /**
     * ブログに紐付いているタグを取得
     */
    @Transactional(readOnly = true)
    public List<Tag> getTagsForBlog(String blogId) {
        return tagService.getTagsForBlog(blogId);
    }

    /**
     * ブログを削除
     */
    public void deleteBlog(String blogId) {
        Blog blog = getBlogById(blogId);
        blogRepository.delete(blog);
    }
}

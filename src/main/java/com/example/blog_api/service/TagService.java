package com.example.blog_api.service;

import com.example.blog_api.dto.BlogTag;
import com.example.blog_api.dto.Tag;
import com.example.blog_api.repository.BlogTagRepository;
import com.example.blog_api.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagService {

    private final TagRepository tagRepository;
    private final BlogTagRepository blogTagRepository;

    public TagService(TagRepository tagRepository, BlogTagRepository blogTagRepository) {
        this.tagRepository = tagRepository;
        this.blogTagRepository = blogTagRepository;
    }

    /**
     * タグ名からTagエンティティを取得または作成
     */
    public Tag getOrCreateTag(String tagName) {
        return tagRepository.findByName(tagName)
                .orElseGet(() -> {
                    Tag newTag = new Tag(tagName);
                    return tagRepository.save(newTag);
                });
    }

    /**
     * ブログにタグを紐付ける
     */
    public void associateTagsWithBlog(String blogId, List<String> tagNames) {
        // 既存のタグ関連付けを削除
        List<BlogTag> existingBlogTags = blogTagRepository.findByBlogId(blogId);
        blogTagRepository.deleteAll(existingBlogTags);

        // 新しいタグを紐付け
        for (String tagName : tagNames) {
            Tag tag = getOrCreateTag(tagName);
            BlogTag blogTag = new BlogTag(blogId, tag.getId());
            blogTagRepository.save(blogTag);
        }
    }

    /**
     * ブログに紐付いているタグを取得
     */
    public List<Tag> getTagsForBlog(String blogId) {
        List<BlogTag> blogTags = blogTagRepository.findByBlogId(blogId);
        List<String> tagIds = blogTags.stream()
                .map(BlogTag::getTagId)
                .collect(Collectors.toList());
        
        return tagRepository.findAllById(tagIds);
    }

    /**
     * タグ名のリストからブログIDのリストを検索（部分一致）
     */
    public List<String> searchBlogIdsByTagNames(List<String> tagNames) {
        List<String> tagIds = new ArrayList<>();
        
        for (String tagName : tagNames) {
            // 部分一致で検索
            List<Tag> matchedTags = tagRepository.findByNameContaining(tagName);
            for (Tag tag : matchedTags) {
                if (!tagIds.contains(tag.getId())) {
                    tagIds.add(tag.getId());
                }
            }
        }
        
        if (tagIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        return blogTagRepository.findBlogIdsByTagIds(tagIds);
    }

    /**
     * 全てのタグを取得
     */
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }
}

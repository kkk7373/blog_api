package com.example.blog_api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.blog_api.dto.Comment;
import com.example.blog_api.exception.ResourceNotFoundException;
import com.example.blog_api.repository.BlogRepository;
import com.example.blog_api.repository.CommentRepository;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final BlogRepository blogRepository;

    public CommentService(CommentRepository commentRepository, BlogRepository blogRepository) {
        this.commentRepository = commentRepository;
        this.blogRepository = blogRepository;
    }

    /**
     * ブログIDでコメントを検索
     */
    @Transactional(readOnly = true)
    public List<Comment> searchCommentByBlogId(String blogId) {
        return commentRepository.findByBlogId(blogId); 
    }

    /**
     * コメントIDでコメントを取得
     */
    @Transactional(readOnly = true)
    public Comment getCommentById(String commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
    }

    /**
     * コメントを作成
     */
    public Comment createComment(String blogId, String userId, String content) {
        // ブログの存在確認
        if (!blogRepository.existsById(blogId)) {
            throw new ResourceNotFoundException("Blog not found");
        }
        
        Comment comment = new Comment();
        comment.setBlogId(blogId);
        comment.setUserId(userId);
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    /**
     * コメントを削除
     */
    public void deleteComment(String commentId) {
        Comment comment = getCommentById(commentId);
        commentRepository.delete(comment);
    }
}

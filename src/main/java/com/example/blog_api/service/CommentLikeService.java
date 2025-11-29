package com.example.blog_api.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.blog_api.dto.CommentLike;
import com.example.blog_api.exception.DuplicateResourceException;
import com.example.blog_api.repository.CommentLikeRepository;

@Service
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;

    public CommentLikeService(CommentLikeRepository commentLikeRepository) {
        this.commentLikeRepository = commentLikeRepository;
    }

    public List<CommentLike> getLikesByCommentId(String commentId) {
        return commentLikeRepository.findByCommentId(commentId);
    }
    
    public CommentLike createCommentLike(String commentId, String userId) {
        // 既にいいね済みかチェック
        if (commentLikeRepository.findByCommentIdAndUserId(commentId, userId).isPresent()) {
            throw new DuplicateResourceException("既にこのコメントにいいね済みです");
        }
        
        CommentLike commentLike = new CommentLike();
        commentLike.setCommentId(commentId);
        commentLike.setUserId(userId);
        commentLike.setCreatedAt(LocalDateTime.now());
        return commentLikeRepository.save(commentLike);
    }
    
    @Transactional
    public void deleteCommentLike(String commentId, String userId) {
        commentLikeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }
}

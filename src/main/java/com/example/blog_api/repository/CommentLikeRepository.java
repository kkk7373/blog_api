package com.example.blog_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.blog_api.dto.CommentLike;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, String> {
    List<CommentLike> findByCommentId(String commentId);
    Optional<CommentLike> findByCommentIdAndUserId(String commentId, String userId);
    void deleteByCommentIdAndUserId(String commentId, String userId);
}

package com.example.blog_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentCreateRequest {
    private String userId;
    
    @NotBlank(message = "コメント内容は必須です")
    @Size(min = 1, max = 1000, message = "コメントは1文字以上1000文字以下である必要があります")
    private String content;

    public CommentCreateRequest() {
    }

    public CommentCreateRequest(String userId, String content) {
        this.userId = userId;
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

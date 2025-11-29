package com.example.blog_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BlogCreateRequest {
    private String userId;
    
    @NotBlank(message = "コンテンツは必須です")
    @Size(min = 1, max = 10000, message = "コンテンツは1文字以上10000文字以下である必要があります")
    private String content;

    public BlogCreateRequest() {
    }

    public BlogCreateRequest(String userId, String content) {
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

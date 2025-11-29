package com.example.blog_api.dto;

public class BlogUpdateRequest {
    private String content;

    public BlogUpdateRequest() {
    }

    public BlogUpdateRequest(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

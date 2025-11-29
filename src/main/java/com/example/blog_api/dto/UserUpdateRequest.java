package com.example.blog_api.dto;

public class UserUpdateRequest {
    private String nickname;
    private String iconUrl;

    public UserUpdateRequest() {
    }

    public UserUpdateRequest(String nickname, String iconUrl) {
        this.nickname = nickname;
        this.iconUrl = iconUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}

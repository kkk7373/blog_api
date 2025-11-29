package com.example.blog_api.security;

import com.example.blog_api.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationHelper {

    private final JwtUtil jwtUtil;

    public AuthenticationHelper(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * 現在の認証ユーザーのユーザー名を取得
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        throw new UnauthorizedException("User is not authenticated");
    }

    /**
     * 現在の認証ユーザーのユーザーIDを取得
     * リクエストヘッダーからJWTトークンを解析して取得
     */
    public String getCurrentUserId(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Authorization header is missing or invalid");
        }
        
        String token = authorizationHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }

    /**
     * 現在のユーザーが指定されたユーザーIDと一致するか確認
     */
    public boolean isCurrentUser(String authorizationHeader, String userId) {
        try {
            String currentUserId = getCurrentUserId(authorizationHeader);
            return currentUserId.equals(userId);
        } catch (Exception e) {
            return false;
        }
    }
}

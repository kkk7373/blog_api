package com.example.blog_api.controller;

import com.example.blog_api.dto.LoginRequest;
import com.example.blog_api.dto.LoginResponse;
import com.example.blog_api.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AuthController 統合テスト")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("ログイン - 成功")
    void login_Success() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setName("testuser");
        request.setPassword("TestPass123");

        LoginResponse response = new LoginResponse();
        response.setToken("jwt-token");
        response.setUserId("user123");

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.userId").value("user123"));
    }

    @Test
    @DisplayName("ログイン - バリデーションエラー（ユーザー名なし）")
    void login_ValidationError_NoUsername() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setPassword("TestPass123");

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value("name: ユーザー名は必須です"));
    }

    @Test
    @DisplayName("ログイン - バリデーションエラー（パスワードなし）")
    void login_ValidationError_NoPassword() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setName("testuser");

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value("password: パスワードは必須です"));
    }
}

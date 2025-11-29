package com.example.blog_api.service;

import com.example.blog_api.dto.LoginRequest;
import com.example.blog_api.dto.LoginResponse;
import com.example.blog_api.dto.User;
import com.example.blog_api.exception.InvalidCredentialsException;
import com.example.blog_api.repository.UserRepository;
import com.example.blog_api.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 単体テスト")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("user123");
        testUser.setName("testuser");
        testUser.setPassword("hashedPassword");

        loginRequest = new LoginRequest();
        loginRequest.setName("testuser");
        loginRequest.setPassword("TestPass123");
    }

    @Test
    @DisplayName("ログイン - 成功")
    void login_Success() {
        // Given
        when(userRepository.findByName("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("TestPass123", "hashedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("testuser", "user123")).thenReturn("jwt-token");

        // When
        LoginResponse result = authService.login(loginRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("jwt-token");
        assertThat(result.getUserId()).isEqualTo("user123");
        verify(userRepository).findByName("testuser");
        verify(passwordEncoder).matches("TestPass123", "hashedPassword");
        verify(jwtUtil).generateToken("testuser", "user123");
    }

    @Test
    @DisplayName("ログイン - ユーザーが見つからない")
    void login_UserNotFound() {
        // Given
        when(userRepository.findByName(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid username or password");

        verify(userRepository).findByName("testuser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    @DisplayName("ログイン - パスワードが不正")
    void login_InvalidPassword() {
        // Given
        when(userRepository.findByName("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("WrongPass123", "hashedPassword")).thenReturn(false);

        loginRequest.setPassword("WrongPass123");

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid username or password");

        verify(userRepository).findByName("testuser");
        verify(passwordEncoder).matches("WrongPass123", "hashedPassword");
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }
}

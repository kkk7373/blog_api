package com.example.blog_api.service;

import com.example.blog_api.dto.User;
import com.example.blog_api.dto.UserCreateRequest;
import com.example.blog_api.exception.DuplicateResourceException;
import com.example.blog_api.exception.ResourceNotFoundException;
import com.example.blog_api.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 単体テスト")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ImageUploadService imageUploadService;

    @Mock
    private PasswordValidationService passwordValidationService;

    @InjectMocks
    private UserService userService;

    private UserCreateRequest validRequest;
    private User existingUser;

    @BeforeEach
    void setUp() {
        validRequest = new UserCreateRequest();
        validRequest.setName("testuser");
        validRequest.setPassword("TestPass123");
        validRequest.setNickname("テストユーザー");

        existingUser = new User();
        existingUser.setId("user123");
        existingUser.setName("testuser");
        existingUser.setNickname("テストユーザー");
        existingUser.setPassword("hashedPassword");
    }

    @Test
    @DisplayName("ユーザー作成 - 成功")
    void createUser_Success() {
        // Given
        when(userRepository.findByName(anyString())).thenReturn(Optional.empty());
        doNothing().when(passwordValidationService).validatePassword(anyString());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // When
        User result = userService.createUser(validRequest, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("testuser");
        verify(userRepository).findByName("testuser");
        verify(passwordValidationService).validatePassword("TestPass123");
        verify(passwordEncoder).encode("TestPass123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("ユーザー作成 - 重複エラー")
    void createUser_DuplicateUser() {
        // Given
        when(userRepository.findByName(anyString())).thenReturn(Optional.of(existingUser));

        // When & Then
        assertThatThrownBy(() -> userService.createUser(validRequest, null))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("User already exists");

        verify(userRepository).findByName("testuser");
        verify(passwordValidationService, never()).validatePassword(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("ユーザー取得 - 成功")
    void getUserById_Success() {
        // Given
        when(userRepository.findById("user123")).thenReturn(Optional.of(existingUser));

        // When
        User result = userService.getUserById("user123");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("user123");
        assertThat(result.getName()).isEqualTo("testuser");
        verify(userRepository).findById("user123");
    }

    @Test
    @DisplayName("ユーザー取得 - 見つからない")
    void getUserById_NotFound() {
        // Given
        when(userRepository.findById("unknown")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUserById("unknown"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository).findById("unknown");
    }

    @Test
    @DisplayName("ユーザー削除 - 成功")
    void deleteUser_Success() {
        // Given
        when(userRepository.findById("user123")).thenReturn(Optional.of(existingUser));
        doNothing().when(userRepository).delete(any(User.class));

        // When
        userService.deleteUser("user123");

        // Then
        verify(userRepository).findById("user123");
        verify(userRepository).delete(existingUser);
    }
}

package com.example.blog_api.service;

import com.example.blog_api.exception.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("PasswordValidationService 単体テスト")
class PasswordValidationServiceTest {

    @InjectMocks
    private PasswordValidationService passwordValidationService;

    @Test
    @DisplayName("パスワード検証 - 成功")
    void validatePassword_Success() {
        // Given
        String validPassword = "TestPass123";

        // When & Then - 例外が発生しないことを確認
        passwordValidationService.validatePassword(validPassword);
    }

    @Test
    @DisplayName("パスワード検証 - null")
    void validatePassword_Null() {
        // When & Then
        assertThatThrownBy(() -> passwordValidationService.validatePassword(null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("パスワードは必須です");
    }

    @Test
    @DisplayName("パスワード検証 - 空文字")
    void validatePassword_Empty() {
        // When & Then
        assertThatThrownBy(() -> passwordValidationService.validatePassword(""))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("パスワードは必須です");
    }

    @Test
    @DisplayName("パスワード検証 - 短すぎる")
    void validatePassword_TooShort() {
        // When & Then
        assertThatThrownBy(() -> passwordValidationService.validatePassword("Test1"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("パスワードは8文字以上である必要があります");
    }

    @Test
    @DisplayName("パスワード検証 - 小文字なし")
    void validatePassword_NoLowercase() {
        // When & Then
        assertThatThrownBy(() -> passwordValidationService.validatePassword("TESTPASS123"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("パスワードには小文字を1文字以上含める必要があります");
    }

    @Test
    @DisplayName("パスワード検証 - 大文字なし")
    void validatePassword_NoUppercase() {
        // When & Then
        assertThatThrownBy(() -> passwordValidationService.validatePassword("testpass123"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("パスワードには大文字を1文字以上含める必要があります");
    }

    @Test
    @DisplayName("パスワード検証 - 数字なし")
    void validatePassword_NoDigit() {
        // When & Then
        assertThatThrownBy(() -> passwordValidationService.validatePassword("TestPassword"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("パスワードには数字を1文字以上含める必要があります");
    }

    @Test
    @DisplayName("パスワード検証 - よくあるパスワード")
    void validatePassword_CommonPassword() {
        // When & Then
        assertThatThrownBy(() -> passwordValidationService.validatePassword("Password123"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("このパスワードは一般的すぎるため使用できません");
    }

    @Test
    @DisplayName("パスワード強度計算 - 弱い")
    void calculatePasswordStrength_Weak() {
        // Given
        String weakPassword = "test1";

        // When
        int strength = passwordValidationService.calculatePasswordStrength(weakPassword);

        // Then
        assertThat(strength).isLessThanOrEqualTo(2);
    }

    @Test
    @DisplayName("パスワード強度計算 - 強い")
    void calculatePasswordStrength_Strong() {
        // Given
        String strongPassword = "TestPass123!@#";

        // When
        int strength = passwordValidationService.calculatePasswordStrength(strongPassword);

        // Then
        assertThat(strength).isGreaterThanOrEqualTo(4);
    }
}

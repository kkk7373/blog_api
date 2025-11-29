package com.example.blog_api.service;

import com.example.blog_api.exception.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PasswordValidationService {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 100;

    /**
     * パスワードの強度を検証
     * @param password 検証するパスワード
     * @throws BadRequestException パスワードが要件を満たさない場合
     */
    public void validatePassword(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.isEmpty()) {
            throw new BadRequestException("パスワードは必須です");
        }

        // 長さチェック
        if (password.length() < MIN_LENGTH) {
            errors.add("パスワードは" + MIN_LENGTH + "文字以上である必要があります");
        }
        if (password.length() > MAX_LENGTH) {
            errors.add("パスワードは" + MAX_LENGTH + "文字以下である必要があります");
        }

        // 小文字を含むか
        if (!password.matches(".*[a-z].*")) {
            errors.add("パスワードには小文字を1文字以上含める必要があります");
        }

        // 大文字を含むか
        if (!password.matches(".*[A-Z].*")) {
            errors.add("パスワードには大文字を1文字以上含める必要があります");
        }

        // 数字を含むか
        if (!password.matches(".*\\d.*")) {
            errors.add("パスワードには数字を1文字以上含める必要があります");
        }

        // よくあるパスワードのチェック
        if (isCommonPassword(password)) {
            errors.add("このパスワードは一般的すぎるため使用できません");
        }

        if (!errors.isEmpty()) {
            throw new BadRequestException(String.join(", ", errors));
        }
    }

    /**
     * よくある脆弱なパスワードかチェック
     */
    private boolean isCommonPassword(String password) {
        List<String> commonPasswords = List.of(
            "Password1", "Password123", "Qwerty123", "Admin123",
            "Welcome1", "Test1234", "User1234", "Pass1234"
        );
        return commonPasswords.stream()
                .anyMatch(common -> common.equalsIgnoreCase(password));
    }

    /**
     * パスワードの強度を評価（オプション）
     * @return 0-4のスコア（0=非常に弱い、4=非常に強い）
     */
    public int calculatePasswordStrength(String password) {
        int score = 0;

        if (password.length() >= 12) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) score++;

        return Math.min(score, 4);
    }
}

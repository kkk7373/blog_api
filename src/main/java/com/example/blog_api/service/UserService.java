package com.example.blog_api.service;

import com.example.blog_api.dto.User;
import com.example.blog_api.dto.UserCreateRequest;
import com.example.blog_api.dto.UserUpdateRequest;
import com.example.blog_api.exception.DuplicateResourceException;
import com.example.blog_api.exception.ResourceNotFoundException;
import com.example.blog_api.exception.BadRequestException;
import com.example.blog_api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageUploadService imageUploadService;
    private final PasswordValidationService passwordValidationService;

    public UserService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder, 
                      ImageUploadService imageUploadService,
                      PasswordValidationService passwordValidationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.imageUploadService = imageUploadService;
        this.passwordValidationService = passwordValidationService;
    }

    public User createUser(UserCreateRequest request, MultipartFile iconFile) {
        // ユーザー名の重複チェック
        if (userRepository.findByName(request.getName()).isPresent()) {
            throw new DuplicateResourceException("User already exists");
        }

        // パスワードの強度チェック
        passwordValidationService.validatePassword(request.getPassword());

        User user = new User();
        user.setName(request.getName());
        user.setNickname(request.getNickname());
        // パスワードをハッシュ化して保存
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // アイコンファイルがある場合はCloudinaryにアップロード
        if (iconFile != null && !iconFile.isEmpty()) {
            try {
                String iconUrl = imageUploadService.uploadImage(iconFile, "user-icons");
                user.setIconUrl(iconUrl);
            } catch (Exception e) {
                throw new BadRequestException("Failed to upload icon image: " + e.getMessage());
            }
        } else if (request.getIconUrl() != null) {
            user.setIconUrl(request.getIconUrl());
        }
        
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User updateUser(String userId, UserUpdateRequest request, MultipartFile iconFile) {
        User user = getUserById(userId);
        
        // 古いアイコンのURLを保存
        String oldIconUrl = user.getIconUrl();

        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        
        // アイコンファイルがある場合
        if (iconFile != null && !iconFile.isEmpty()) {
            try {
                // 新しい画像をアップロード
                String newIconUrl = imageUploadService.uploadImage(iconFile, "user-icons");
                user.setIconUrl(newIconUrl);
                
                // 古い画像を削除
                if (oldIconUrl != null && !oldIconUrl.isEmpty()) {
                    try {
                        imageUploadService.deleteImage(oldIconUrl);
                    } catch (Exception e) {
                        // 削除失敗してもエラーにしない
                        System.err.println("Failed to delete old icon: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                throw new BadRequestException("Failed to upload icon image: " + e.getMessage());
            }
        } else if (request.getIconUrl() != null) {
            user.setIconUrl(request.getIconUrl());
        }
        
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public void deleteUser(String userId) {
        User user = getUserById(userId);
        
        // ユーザー削除時にアイコン画像も削除
        if (user.getIconUrl() != null && !user.getIconUrl().isEmpty()) {
            try {
                imageUploadService.deleteImage(user.getIconUrl());
            } catch (Exception e) {
                // 削除失敗してもエラーにしない
                System.err.println("Failed to delete user icon: " + e.getMessage());
            }
        }
        
        userRepository.delete(user);
    }
}

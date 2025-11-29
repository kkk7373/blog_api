package com.example.blog_api.repository;

import com.example.blog_api.dto.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("UserRepository テスト")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("ユーザー名で検索 - 見つかる")
    void findByName_Found() {
        // Given
        User user = new User();
        user.setName("testuser");
        user.setNickname("テストユーザー");
        user.setPassword("hashedPassword");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        entityManager.persist(user);
        entityManager.flush();

        // When
        Optional<User> found = userRepository.findByName("testuser");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("testuser");
        assertThat(found.get().getNickname()).isEqualTo("テストユーザー");
    }

    @Test
    @DisplayName("ユーザー名で検索 - 見つからない")
    void findByName_NotFound() {
        // When
        Optional<User> found = userRepository.findByName("nonexistent");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("ユーザー保存")
    void saveUser() {
        // Given
        User user = new User();
        user.setName("newuser");
        user.setNickname("新しいユーザー");
        user.setPassword("hashedPassword");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // When
        User saved = userRepository.save(user);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("newuser");
        assertThat(saved.getNickname()).isEqualTo("新しいユーザー");
    }
}

package com.example.blog_api.repository;

import com.example.blog_api.dto.Blog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("BlogRepository テスト")
class BlogRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BlogRepository blogRepository;

    @Test
    @DisplayName("ユーザーIDでブログ検索")
    void findByUserId() {
        // Given
        Blog blog1 = new Blog();
        blog1.setUserId("user123");
        blog1.setContent("Blog 1 content");
        blog1.setCreatedAt(LocalDateTime.now());
        blog1.setUpdatedAt(LocalDateTime.now());
        entityManager.persist(blog1);

        Blog blog2 = new Blog();
        blog2.setUserId("user123");
        blog2.setContent("Blog 2 content");
        blog2.setCreatedAt(LocalDateTime.now());
        blog2.setUpdatedAt(LocalDateTime.now());
        entityManager.persist(blog2);

        Blog blog3 = new Blog();
        blog3.setUserId("user456");
        blog3.setContent("Blog 3 content");
        blog3.setCreatedAt(LocalDateTime.now());
        blog3.setUpdatedAt(LocalDateTime.now());
        entityManager.persist(blog3);

        entityManager.flush();

        // When
        List<Blog> found = blogRepository.findByUserId("user123");

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).allMatch(blog -> blog.getUserId().equals("user123"));
    }

    @Test
    @DisplayName("ブログIDのリストでブログ検索")
    void findByIdIn() {
        // Given
        Blog blog1 = new Blog();
        blog1.setUserId("user123");
        blog1.setContent("Blog 1");
        blog1.setCreatedAt(LocalDateTime.now());
        blog1.setUpdatedAt(LocalDateTime.now());
        entityManager.persist(blog1);

        Blog blog2 = new Blog();
        blog2.setUserId("user456");
        blog2.setContent("Blog 2");
        blog2.setCreatedAt(LocalDateTime.now());
        blog2.setUpdatedAt(LocalDateTime.now());
        entityManager.persist(blog2);

        entityManager.flush();

        String blog1Id = blog1.getId();
        String blog2Id = blog2.getId();

        // When
        List<Blog> found = blogRepository.findByIdIn(List.of(blog1Id, blog2Id));

        // Then
        assertThat(found).hasSize(2);
    }

    @Test
    @DisplayName("ブログ保存")
    void saveBlog() {
        // Given
        Blog blog = new Blog();
        blog.setUserId("user123");
        blog.setContent("New blog content");
        blog.setCreatedAt(LocalDateTime.now());
        blog.setUpdatedAt(LocalDateTime.now());

        // When
        Blog saved = blogRepository.save(blog);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getContent()).isEqualTo("New blog content");
        assertThat(saved.getUserId()).isEqualTo("user123");
    }
}

package com.example.blog_api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class TagGenerationService {

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    private final RestTemplate restTemplate;

    public TagGenerationService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * ブログの内容からタグを自動生成（最大5つ）
     */
    public List<String> generateTags(String content) {
        try {
            String prompt = String.format(
                "以下のブログ記事の内容から、適切なタグを最大5つ生成してください。" +
                "タグは、カンマ区切りで出力してください。タグは日本語で、簡潔な単語またはフレーズにしてください。\n\n" +
                "内容: %s\n\nタグ:",
                content.length() > 1000 ? content.substring(0, 1000) + "..." : content
            );

            String url = String.format(
                "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                model, apiKey
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                    Map.of(
                        "parts", List.of(
                            Map.of("text", prompt)
                        )
                    )
                )
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return parseGeminiResponse(response.getBody());
            } else {
                System.err.println("Gemini API エラー: " + response.getStatusCode());
                return Arrays.asList("ブログ");
            }
        } catch (Exception e) {
            System.err.println("タグ生成に失敗しました: " + e.getMessage());
            e.printStackTrace();
            return Arrays.asList("ブログ");
        }
    }

    /**
     * Gemini APIのレスポンスからタグをパース
     */
    @SuppressWarnings("unchecked")
    private List<String> parseGeminiResponse(Map<String, Object> responseBody) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                if (parts != null && !parts.isEmpty()) {
                    String text = (String) parts.get(0).get("text");
                    return parseTags(text);
                }
            }
        } catch (Exception e) {
            System.err.println("レスポンスのパースに失敗: " + e.getMessage());
        }
        return Arrays.asList("ブログ");
    }

    /**
     * テキストからタグをパース
     */
    private List<String> parseTags(String text) {
        String[] tags = text.split("[,、\\n]");
        List<String> result = new ArrayList<>();
        
        for (String tag : tags) {
            String trimmed = tag.trim()
                .replaceAll("^[0-9]+\\.\\s*", "") // 番号付きリストを除去
                .replaceAll("^[-*]\\s*", "")      // 箇条書き記号を除去
                .replaceAll("[\"'`]", "");         // クォート除去
            
            if (!trimmed.isEmpty() && result.size() < 5) {
                result.add(trimmed);
            }
        }
        
        return result.isEmpty() ? Arrays.asList("ブログ") : result;
    }
}

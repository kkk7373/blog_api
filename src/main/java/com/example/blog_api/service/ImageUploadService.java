package com.example.blog_api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.blog_api.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class ImageUploadService {

    private final Cloudinary cloudinary;

    public ImageUploadService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * 画像をCloudinaryにアップロードしてURLを返す
     * @param file アップロードする画像ファイル
     * @param folder Cloudinary上のフォルダ名（例: "user-icons"）
     * @return アップロードされた画像のURL
     */
    public String uploadImage(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // ファイルの検証
        validateImage(file);

        // ユニークなファイル名を生成
        String publicId = folder + "/" + UUID.randomUUID().toString();

        // Cloudinaryにアップロード
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "public_id", publicId,
                        "folder", folder,
                        "resource_type", "image",
                        "transformation", ObjectUtils.asMap(
                                "width", 500,
                                "height", 500,
                                "crop", "limit",
                                "quality", "auto"
                        )
                ));

        // アップロードされた画像のURLを返す
        return (String) uploadResult.get("secure_url");
    }

    /**
     * Cloudinaryから画像を削除
     * @param imageUrl 削除する画像のURL
     */
    public void deleteImage(String imageUrl) throws IOException {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        // URLからpublic_idを抽出
        String publicId = extractPublicId(imageUrl);
        if (publicId != null) {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        }
    }

    /**
     * 画像ファイルの検証
     */
    private void validateImage(MultipartFile file) {
        // ファイルサイズチェック（5MB以下）
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new BadRequestException("File size exceeds maximum limit of 5MB");
        }

        // ファイルタイプチェック
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("Only image files are allowed");
        }

        // 許可された画像形式
        if (!contentType.equals("image/jpeg") &&
            !contentType.equals("image/jpg") &&
            !contentType.equals("image/png") &&
            !contentType.equals("image/gif") &&
            !contentType.equals("image/webp")) {
            throw new BadRequestException("Unsupported image format. Allowed: JPEG, PNG, GIF, WebP");
        }
    }

    /**
     * CloudinaryのURLからpublic_idを抽出
     */
    private String extractPublicId(String imageUrl) {
        try {
            // https://res.cloudinary.com/{cloud_name}/image/upload/v{version}/{folder}/{filename}.{ext}
            String[] parts = imageUrl.split("/upload/");
            if (parts.length < 2) {
                return null;
            }
            String path = parts[1];
            // バージョン番号を削除（v1234567890/...）
            if (path.matches("^v\\d+/.*")) {
                path = path.substring(path.indexOf('/') + 1);
            }
            // 拡張子を削除
            int lastDot = path.lastIndexOf('.');
            if (lastDot > 0) {
                path = path.substring(0, lastDot);
            }
            return path;
        } catch (Exception e) {
            return null;
        }
    }
}

# Blog API

ブログ、ユーザー、コメント、いいね機能を提供する RESTful API です。JWT 認証、画像アップロード、AI 自動タグ生成機能を備えています。

## 主な機能

- **ユーザー管理**: 登録、ログイン、プロフィール編集、アイコン画像アップロード
- **ブログ投稿**: CRUD 操作、AI 自動タグ生成（Gemini API）
- **コメント機能**: ブログへのコメント投稿・削除
- **いいね機能**: ブログ・コメントへのいいね
- **タグ検索**: 部分一致によるブログ検索
- **JWT 認証**: セキュアな認証・認可システム
- **画像ストレージ**: Cloudinary による画像管理

## 技術スタック

### バックエンド

- **Java**: 17 (Runtime: 24)
- **Spring Boot**: 3.2.0
- **Spring Security**: JWT 認証
- **Hibernate**: 6.3.1 (ORM)

### データベース

- **MySQL**: 9.3.0 (本番環境)
- **H2**: インメモリ DB (テスト環境)

### 外部サービス連携

- **Cloudinary**: 画像ストレージ
- **Google Gemini API**: AI 自動タグ生成 (gemini-2.5-flash)

### 主要ライブラリ

- **jjwt**: 0.12.3 (JWT 認証)
- **cloudinary-http44**: 1.36.0
- **google-cloud-aiplatform**: 3.30.0
- **springdoc-openapi**: 2.3.0 (API 仕様書生成)

### テストフレームワーク

- **JUnit 5**: テスティングフレームワーク
- **Mockito**: 5.14.2 (モックライブラリ)
- **Spring Boot Test**: 統合テスト
- **AssertJ**: アサーションライブラリ
- **MockMvc**: Controller 統合テスト
- **@DataJpaTest**: Repository 単体テスト

### ツール

- **Maven**: ビルドツール
- **Swagger UI**: API 仕様確認

## アーキテクチャ

### レイヤー構成

```
Controller層 → Service層 → Repository層 → Database
     ↓
  DTO/Entity
```

### 認証フロー

```
Client → JWT Filter → Controller → Service
              ↓
    Security Context (認証情報)
```

### 特徴的な機能

#### 🤖 AI 自動タグ生成

ブログ投稿時、Gemini API を使用して自動的に最大 5 つのタグを生成します。

```json
POST /blogs
{
  "content": "Spring Bootを使ったマイクロサービスの開発について..."
}

// 自動生成されるタグの例
["Spring Boot", "マイクロサービス", "フレームワーク", "Java", "Webアプリケーション"]
```

#### 🔍 部分一致タグ検索

タグ名の部分一致で検索できます。

```
GET /blogs/search?tags=Spring
→ "Spring Boot", "Spring Framework", "Spring MVC" などのタグを持つブログを返却
```

#### 🖼️ 画像アップロード

ユーザーのアイコン画像を Cloudinary にアップロードし、URL をデータベースに保存します。

- 対応形式: JPEG, PNG, GIF, WebP
- 最大サイズ: 5MB
- 自動リサイズ: 500x500px（クロップ）

#### 🔒 セキュリティ

- **パスワードハッシュ化**: BCrypt (ストレングス 12) でハッシュ化して保存
- **パスワードポリシー**:
  - 8〜100 文字
  - 小文字、大文字、数字をそれぞれ 1 文字以上含む
  - よくある脆弱なパスワードはブロック
- **JWT 認証**: Bearer トークンによる安全なアクセス制御
- **所有者検証**: リソースの所有者のみが編集・削除可能
- **一意制約**: 重複いいね・重複ユーザー名を防止
- **セキュリティヘッダー**:
  - Frame Options: DENY
  - Content Security Policy: default-src 'self'
- **バリデーション**:
  - ユーザー名: 3〜20 文字、英数字とアンダースコアのみ
  - ニックネーム: 1〜50 文字
  - ブログコンテンツ: 1〜10000 文字
  - コメント: 1〜1000 文字

#### ⚠️ エラーハンドリング

カスタム例外クラスとグローバル例外ハンドラにより、統一されたエラーレスポンスを返します。

```json
{
  "timestamp": "2025-11-29T23:10:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found",
  "path": "/users/999"
}
```

**主なエラーレスポンス**:

- `400 Bad Request`: バリデーションエラー、不正なリクエスト
- `401 Unauthorized`: 認証情報が不正
- `403 Forbidden`: 権限がない
- `404 Not Found`: リソースが見つからない
- `409 Conflict`: リソースの重複（いいね重複、ユーザー名重複など）
- `500 Internal Server Error`: 予期しないエラー

## データベーススキーマ

### テーブル一覧

- `users` - ユーザー情報
- `blogs` - ブログ記事
- `comments` - コメント
- `blog_likes` - ブログへのいいね
- `comment_likes` - コメントへのいいね
- `tags` - タグマスター
- `blog_tags` - ブログとタグの中間テーブル

### 主要な制約

- `users`: name の一意制約
- `blog_likes`: (blog_id, user_id) の一意制約
- `comment_likes`: (comment_id, user_id) の一意制約
- `tags`: name の一意制約

## API エンドポイント

詳細な API 仕様は Swagger UI で確認できます：

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI 仕様**: http://localhost:8080/openapi/api.yml

### 主要エンドポイント

| カテゴリ     | メソッド | パス                          | 説明                       | 認証 |
| ------------ | -------- | ----------------------------- | -------------------------- | ---- |
| **認証**     | POST     | `/auth/login`                 | ログイン                   | -    |
| **ユーザー** | POST     | `/users`                      | ユーザー登録               | -    |
|              | GET      | `/users/{userId}`             | ユーザー情報取得           | -    |
|              | PUT      | `/users/{userId}`             | ユーザー情報更新           | ✓    |
|              | DELETE   | `/users/{userId}`             | ユーザー削除               | ✓    |
| **ブログ**   | GET      | `/blogs`                      | ブログ一覧取得             | -    |
|              | POST     | `/blogs`                      | ブログ投稿（タグ自動生成） | ✓    |
|              | GET      | `/blogs/{blogId}`             | ブログ詳細取得             | -    |
|              | GET      | `/blogs/{blogId}/tags`        | ブログのタグ一覧           | -    |
|              | GET      | `/blogs/search?tags=...`      | タグ検索（部分一致）       | -    |
|              | PUT      | `/blogs/{blogId}`             | ブログ更新                 | ✓    |
|              | DELETE   | `/blogs/{blogId}`             | ブログ削除                 | ✓    |
| **コメント** | GET      | `/blogs/{blogId}/comments`    | コメント一覧               | -    |
|              | POST     | `/blogs/{blogId}/comments`    | コメント投稿               | ✓    |
|              | GET      | `/comments/{commentId}`       | コメント詳細               | -    |
|              | DELETE   | `/comments/{commentId}`       | コメント削除               | ✓    |
| **いいね**   | GET      | `/blogs/{blogId}/likes`       | ブログのいいね一覧         | -    |
|              | POST     | `/blogs/{blogId}/likes`       | ブログにいいね             | ✓    |
|              | DELETE   | `/blogs/{blogId}/likes`       | いいね解除                 | ✓    |
|              | GET      | `/comments/{commentId}/likes` | コメントのいいね一覧       | -    |
|              | POST     | `/comments/{commentId}/likes` | コメントにいいね           | ✓    |
|              | DELETE   | `/comments/{commentId}/likes` | いいね解除                 | ✓    |

## セットアップと実行

### 前提条件

- Java 17 以上
- Maven 3.8 以上
- MySQL 9.3.0 以上

### 環境変数の設定

`src/main/resources/application.yml.example` を参考に、`application.yml` を作成してください。

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/blog_db
    username: your_username
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update

jwt:
  secret: your-256-bit-secret-key-here-make-it-long-enough
  expiration: 86400000 # 24時間

cloudinary:
  cloud-name: your_cloud_name
  api-key: your_api_key
  api-secret: your_api_secret

gemini:
  api-key: your_gemini_api_key
  project-id: your_project_id
  location: us-central1
```

### データベースの準備

```bash
# MySQLにログイン
mysql -u root -p

# データベースを作成
CREATE DATABASE blog_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# ユーザーに権限を付与（必要に応じて）
GRANT ALL PRIVILEGES ON blog_db.* TO 'your_username'@'localhost';
FLUSH PRIVILEGES;
```

### アプリケーションの起動

```bash
# 依存関係のインストールとビルド
./mvnw clean install

# アプリケーションの起動
./mvnw spring-boot:run
```

アプリケーションは http://localhost:8080 で起動します。

### テストの実行

```bash
# 全テストを実行
./mvnw test

# 特定のテストクラスを実行
./mvnw test -Dtest=UserServiceTest

# テストをスキップしてビルド
./mvnw clean package -DskipTests
```

#### テストカバレッジ

プロジェクトには 47 個のテストケースが含まれており、以下のレイヤーをカバーしています：

- **Repository 層テスト** (6 テスト)

  - `UserRepositoryTest`: ユーザーの CRUD 操作
  - `BlogRepositoryTest`: ブログの CRUD 操作

- **Service 層テスト** (24 テスト)

  - `UserServiceTest`: ユーザー作成、取得、削除のテスト
  - `AuthServiceTest`: ログイン機能のテスト
  - `BlogServiceTest`: ブログ CRUD 操作のテスト
  - `PasswordValidationServiceTest`: パスワードバリデーションのテスト

- **Controller 層テスト** (8 テスト)

  - `AuthControllerTest`: 認証エンドポイントの統合テスト
  - `BlogControllerTest`: ブログエンドポイントの統合テスト

- **Exception 層テスト** (8 テスト)

  - `GlobalExceptionHandlerTest`: 全例外タイプのハンドリングテスト

- **起動確認テスト** (1 テスト)
  - `BlogApiApplicationTests`: アプリケーション起動テスト

すべてのテストは H2 インメモリデータベースを使用し、本番データベースに影響を与えません。

## API 使用例

### 1. ユーザー登録

```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "testuser",
    "nickname": "テストユーザー",
    "password": "SecurePass123"
  }'
```

### 2. ログイン

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "name": "testuser",
    "password": "SecurePass123"
  }'
```

レスポンス例：

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 86400000
}
```

### 3. ブログ投稿（認証必要）

```bash
curl -X POST http://localhost:8080/blogs \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "content": "Spring Bootを使った開発は効率的です。"
  }'
```

### 4. タグ検索

```bash
curl -X GET "http://localhost:8080/blogs/search?tags=Spring"
```

### 5. ユーザーアイコンのアップロード

```bash
curl -X PUT http://localhost:8080/users/{userId} \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "icon=@/path/to/image.jpg"
```

## トラブルシューティング

### ポート 8080 が使用中の場合

```bash
# プロセスを確認
lsof -ti:8080

# プロセスを停止
lsof -ti:8080 | xargs kill -9
```

### データベース接続エラー

1. MySQL が起動していることを確認
2. `application.yml`の接続情報を確認
3. データベースとユーザーが作成されていることを確認

### JWT 認証エラー

1. トークンが期限切れでないか確認
2. `Authorization`ヘッダーの形式が `Bearer {token}` になっているか確認
3. `application.yml`の JWT 秘密鍵が正しく設定されているか確認

## ドキュメント

- [エラーハンドリング詳細](ERROR_HANDLING_EXAMPLES.md)
- [セキュリティとバリデーション](SECURITY_VALIDATION_IMPROVEMENTS.md)
- [OpenAPI 仕様書](src/main/resources/openapi/api.yml)

## ライセンス

このプロジェクトは MIT ライセンスの下で公開されています。


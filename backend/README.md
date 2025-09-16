# Novel Reading App - Backend API

## Tổng quan
Backend API cho ứng dụng đọc truyện online được xây dựng bằng Kotlin, Spring Boot và MongoDB.

## Công nghệ sử dụng
- **Kotlin** 2.0.21
- **Spring Boot** 3.3.5
- **Spring Data MongoDB**
- **MongoDB**
- **Gradle**

## Cấu trúc dự án
```
backend/
├── src/main/kotlin/com/miraimagiclab/novelreadingapp/
│   ├── config/                 # Cấu hình
│   ├── controller/             # REST Controllers
│   ├── dto/                    # Data Transfer Objects
│   ├── exception/              # Exception handling
│   ├── model/                  # Entity models
│   ├── repository/             # Data access layer
│   ├── service/                # Business logic
│   └── NovelReadingAppApplication.kt
├── src/main/resources/
│   └── application.yml         # Application configuration
└── build.gradle.kts           # Gradle build file
```

## Cài đặt và chạy

### Yêu cầu
- Java 17+
- MongoDB 4.4+
- Gradle 7.0+

### Cài đặt MongoDB
1. Cài đặt MongoDB Community Server
2. Khởi động MongoDB service
3. Tạo database `novel_reading_db`

### Chạy ứng dụng
```bash
cd backend
./gradlew bootRun
```

API sẽ chạy tại: `http://localhost:8080/api`

## API Endpoints

### Novel Management
- `POST /api/novels` - Tạo novel mới
- `GET /api/novels/{id}` - Lấy thông tin novel theo ID
- `PUT /api/novels/{id}` - Cập nhật novel
- `DELETE /api/novels/{id}` - Xóa novel
- `GET /api/novels` - Lấy danh sách tất cả novels (có phân trang)

### Search & Filter
- `POST /api/novels/search` - Tìm kiếm novels với nhiều tiêu chí
- `GET /api/novels/author/{authorId}` - Lấy novels theo tác giả

### Top Lists
- `GET /api/novels/top/view-count` - Top novels theo lượt xem
- `GET /api/novels/top/follow-count` - Top novels theo lượt follow
- `GET /api/novels/top/rating` - Top novels theo đánh giá
- `GET /api/novels/recent` - Novels được cập nhật gần đây

### Interactions
- `POST /api/novels/{id}/view` - Tăng lượt xem
- `POST /api/novels/{id}/follow` - Tăng lượt follow
- `DELETE /api/novels/{id}/follow` - Giảm lượt follow
- `POST /api/novels/{id}/rating?rating={value}` - Cập nhật đánh giá

### Health Check
- `GET /api/health` - Kiểm tra trạng thái API

## Model Structure

### Novel Entity
```kotlin
data class Novel(
    val id: String?,
    val title: String,
    val description: String,
    val authorName: String,
    val coverImage: String?,
    val categories: Set<CategoryEnum>,
    val viewCount: Int,
    val followCount: Int,
    val commentCount: Int,
    val rating: Double,
    val ratingCount: Int,
    val wordCount: Int,
    val chapterCount: Int,
    val authorId: String?,
    val status: NovelStatusEnum,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val isR18: Boolean
)
```

### Categories
- ROMANCE, FANTASY, MYSTERY, THRILLER, HORROR
- SCIENCE_FICTION, HISTORICAL, CONTEMPORARY
- YOUNG_ADULT, ADULT, COMEDY, DRAMA
- ACTION, ADVENTURE, SUPERNATURAL, SLICE_OF_LIFE
- SCHOOL_LIFE, REINCARNATION, TRANSMIGRATION
- SYSTEM, CULTIVATION, WUXIA, XIANXIA
- URBAN, MODERN, ANCIENT, OTHER

### Novel Status
- DRAFT, PUBLISHED, ONGOING, COMPLETED, HIATUS, CANCELLED

## Response Format
Tất cả API responses đều có format:
```json
{
  "success": true,
  "message": "Success message",
  "data": { ... },
  "timestamp": "2024-01-01T00:00:00",
  "errors": null
}
```

## Error Handling
API sử dụng HTTP status codes chuẩn:
- `200 OK` - Thành công
- `201 Created` - Tạo mới thành công
- `400 Bad Request` - Dữ liệu không hợp lệ
- `404 Not Found` - Không tìm thấy
- `409 Conflict` - Xung đột dữ liệu
- `500 Internal Server Error` - Lỗi server

## CORS Configuration
API được cấu hình để hỗ trợ CORS cho frontend applications.

# Swagger UI Guide - Novel Reading App API

## Tổng quan
Swagger UI đã được cấu hình cho Novel Reading App API để giúp bạn test và tương tác với các endpoints một cách dễ dàng. 

**Lưu ý**: Chúng ta sử dụng SpringDoc OpenAPI với cấu hình tối giản - chỉ cần các Spring annotations cơ bản như `@RestController`, `@RequestMapping`, `@GetMapping`, etc. SpringDoc sẽ tự động generate documentation mà không cần thêm nhiều Swagger annotations phức tạp.

## Cách truy cập Swagger UI

### 1. Khởi động Backend API
```bash
cd backend
./gradlew bootRun
```

### 2. Truy cập Swagger UI
Sau khi backend đã khởi động thành công, bạn có thể truy cập Swagger UI tại:

**URL chính:**
- http://localhost:8080/api/swagger-ui.html

**Các URL khác:**
- API Documentation (JSON): http://localhost:8080/api/api-docs
- API Documentation (YAML): http://localhost:8080/api/api-docs.yaml

## Các tính năng của Swagger UI

### 1. Xem tất cả APIs
- Swagger UI sẽ hiển thị tất cả các endpoints được nhóm theo tags:
  - **Novel Management**: Các API quản lý novel
  - **Health Check**: API kiểm tra trạng thái service

### 2. Test APIs trực tiếp
- Click vào bất kỳ endpoint nào để xem chi tiết
- Click nút "Try it out" để test API
- Điền các tham số cần thiết
- Click "Execute" để gửi request

### 3. Xem Response Examples
- Mỗi endpoint đều có ví dụ về request/response
- Có thể xem schema của các DTO objects

## Các API Endpoints chính

### Novel Management APIs

#### 1. Tạo Novel mới
- **POST** `/api/novels`
- Body: NovelCreateRequest
- Response: NovelDto với status 201

#### 2. Lấy Novel theo ID
- **GET** `/api/novels/{id}`
- Path parameter: id (String)
- Response: NovelDto

#### 3. Cập nhật Novel
- **PUT** `/api/novels/{id}`
- Path parameter: id (String)
- Body: NovelUpdateRequest
- Response: NovelDto

#### 4. Xóa Novel
- **DELETE** `/api/novels/{id}`
- Path parameter: id (String)
- Response: Success message

#### 5. Lấy tất cả Novels (có phân trang)
- **GET** `/api/novels?page=0&size=20`
- Query parameters: page (int), size (int)
- Response: PageResponse<NovelDto>

#### 6. Tìm kiếm Novels
- **POST** `/api/novels/search`
- Body: NovelSearchRequest
- Response: PageResponse<NovelDto>

#### 7. Lấy Novels theo tác giả
- **GET** `/api/novels/author/{authorId}?page=0&size=20`
- Path parameter: authorId (String)
- Query parameters: page (int), size (int)
- Response: PageResponse<NovelDto>

#### 8. Top Novels
- **GET** `/api/novels/top/view-count` - Top theo lượt xem
- **GET** `/api/novels/top/follow-count` - Top theo lượt follow
- **GET** `/api/novels/top/rating` - Top theo rating
- **GET** `/api/novels/recent` - Novels mới cập nhật

#### 9. Tương tác với Novel
- **POST** `/api/novels/{id}/view` - Tăng lượt xem
- **POST** `/api/novels/{id}/follow` - Tăng lượt follow
- **DELETE** `/api/novels/{id}/follow` - Giảm lượt follow
- **POST** `/api/novels/{id}/rating?rating=4.5` - Cập nhật rating

### Health Check API
- **GET** `/api/health` - Kiểm tra trạng thái service

## Cấu hình Swagger

### File cấu hình chính
- `OpenApiConfig.kt`: Cấu hình thông tin API, servers, contact
- `application.yml`: Cấu hình Swagger UI paths và options

### Customization
Bạn có thể tùy chỉnh thêm trong `OpenApiConfig.kt`:
- Thêm servers mới
- Cập nhật thông tin contact
- Thêm security schemes
- Cấu hình global parameters

### Minimal Configuration Approach
Chúng ta sử dụng cách tiếp cận tối giản:
- **Chỉ cần**: `@Tag` annotation cho việc nhóm APIs
- **Tự động**: SpringDoc sẽ detect tất cả endpoints từ Spring annotations
- **Không cần**: `@Operation`, `@ApiResponse`, `@Parameter` annotations phức tạp
- **Kết quả**: Code sạch hơn, dễ maintain hơn, vẫn có đầy đủ documentation

## Troubleshooting

### 1. Swagger UI không load được
- Kiểm tra backend đã chạy chưa
- Kiểm tra port 8080 có bị chiếm không
- Kiểm tra context-path trong application.yml

### 2. APIs không hiển thị
- Kiểm tra annotations @Operation, @Tag đã được thêm chưa
- Kiểm tra package scanning trong main application class

### 3. Test API bị lỗi
- Kiểm tra MongoDB đã chạy chưa
- Kiểm tra database connection trong application.yml
- Xem logs trong console để debug

## Tips sử dụng

1. **Sử dụng "Try it out"**: Click vào endpoint → "Try it out" → điền parameters → "Execute"
2. **Xem Response Schema**: Scroll xuống để xem cấu trúc response
3. **Copy cURL**: Swagger UI có thể generate cURL command cho bạn
4. **Download OpenAPI spec**: Có thể download file JSON/YAML để import vào Postman

Chúc bạn test API thành công! 🚀

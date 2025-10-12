# Novel Reading App - MVVM Architecture

## 📱 Tổng quan dự án

Novel Reading App là ứng dụng đọc truyện online được xây dựng với kiến trúc MVVM chuẩn, sử dụng Jetpack Compose cho UI và kết nối với backend API Spring Boot.

## 🏗️ Kiến trúc MVVM

### Kiến trúc tổng quan

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Presentation  │    │     Domain      │    │      Data       │
│     Layer       │    │     Layer       │    │     Layer       │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│ • ViewModels    │◄──►│ • Models        │◄──►│ • Repository    │
│ • UI Screens    │    │ • Interfaces    │    │ • API Service   │
│ • Components    │    │ • Use Cases     │    │ • Local DB      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### 1. 📱 Presentation Layer (UI Layer)

**Vai trò**: Xử lý giao diện người dùng và tương tác

#### Components:
- **ViewModels**: Quản lý UI state và business logic
  - `HomeViewModel`: Quản lý state cho HomeScreen
  - Sử dụng `StateFlow` cho reactive programming
  - Inject dependencies thông qua Hilt

- **UI Screens**: Các màn hình của ứng dụng
  - `HomeScreen`: Màn hình chính hiển thị danh sách truyện
  - `BookDetailsScreen`: Chi tiết truyện
  - `ReadingScreen`: Màn hình đọc truyện

- **UI Components**: Các component tái sử dụng
  - `BookCard`: Card hiển thị thông tin truyện
  - `BannerCard`: Banner quảng cáo
  - `BottomNavigationBar`: Thanh điều hướng

#### Công nghệ sử dụng:
- **Jetpack Compose**: Modern UI toolkit
- **Navigation Compose**: Điều hướng giữa các màn hình
- **Material 3**: Design system

### 2. 🎯 Domain Layer

**Vai trò**: Chứa business logic và models độc lập

#### Components:
- **Models**: Domain entities
  - `Novel`: Model truyện chính
  - `NovelStatus`: Enum trạng thái truyện
  - Clean models không phụ thuộc framework

- **Repository Interfaces**: Contract cho data access
  - `NovelRepository`: Interface định nghĩa các operations

- **Use Cases**: Business logic (nếu cần)
  - Có thể mở rộng thêm các use case cụ thể

#### Lợi ích:
- **Separation of Concerns**: Tách biệt business logic khỏi UI
- **Testability**: Dễ dàng unit test
- **Independence**: Không phụ thuộc vào framework cụ thể

### 3. 💾 Data Layer

**Vai trò**: Quản lý dữ liệu từ nhiều nguồn

#### 3.1 Remote Data Source
- **API Service**: Kết nối với backend
  - `NovelApiService`: Retrofit interface
  - Endpoints: `/novels/top/rating`, `/novels/recent`, etc.
  
- **DTOs**: Data Transfer Objects
  - `NovelDto`: Mapping với API response
  - `ApiResponse<T>`: Wrapper cho API response
  - `PageResponse<T>`: Phân trang

#### 3.2 Local Data Source
- **Room Database**: Cache dữ liệu offline
  - `NovelEntity`: Entity cho database
  - `NovelDao`: Data Access Object
  - `AppDatabase`: Database configuration

#### 3.3 Repository Implementation
- **Single Source of Truth**: `NovelRepositoryImpl`
- **Offline-first Strategy**: Ưu tiên cache, fallback API
- **Error Handling**: Xử lý lỗi network và database

#### Công nghệ sử dụng:
- **Retrofit**: HTTP client cho API calls
- **OkHttp**: Network layer với logging
- **Room**: Local database
- **Gson**: JSON serialization

## 🔧 Dependency Injection với Hilt

### Modules:
- **NetworkModule**: Cung cấp Retrofit, OkHttp, API services
- **DatabaseModule**: Cung cấp Room database, DAOs
- **AppModule**: Bind repository implementations

### Lợi ích:
- **Automatic Dependency Management**: Tự động inject dependencies
- **Testability**: Dễ dàng mock dependencies cho testing
- **Lifecycle Management**: Tự động quản lý lifecycle của dependencies

## 📊 State Management

### UiState Pattern:
```kotlin
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

### StateFlow:
- **Reactive Programming**: UI tự động update khi data thay đổi
- **Lifecycle-aware**: Tự động cleanup khi không cần thiết
- **Thread-safe**: Hoạt động an toàn với coroutines

## 🌐 API Integration

### Backend Mapping:
| UI Section | API Endpoint | Mục đích |
|------------|--------------|----------|
| Banner | `/novels/top/rating` | Top truyện theo rating |
| Recommended | `/novels/top/follow-count` | Truyện được follow nhiều |
| New Books | `/novels/recent` | Truyện mới cập nhật |
| Completed | `/novels?status=COMPLETED` | Truyện đã hoàn thành |
| Ranking | `/novels/top/view-count` | Top truyện theo lượt xem |

### Base URL:
- **Development**: `http://10.0.2.2:8080/api/` (Android Emulator)
- **Production**: Có thể config thông qua Build Variants

## 🚀 Tính năng chính

### 1. Offline-first Architecture
- **Cache Strategy**: Lưu trữ dữ liệu locally
- **Network Fallback**: Hoạt động khi mất mạng
- **Background Sync**: Đồng bộ dữ liệu khi có mạng

### 2. Reactive UI
- **Real-time Updates**: UI cập nhật ngay khi có data mới
- **Loading States**: Hiển thị loading indicators
- **Error Handling**: Xử lý lỗi với retry mechanism

### 3. Clean Architecture
- **Separation of Concerns**: Tách biệt các layer
- **SOLID Principles**: Tuân thủ nguyên tắc SOLID
- **Maintainability**: Dễ dàng maintain và extend

## 🛠️ Công nghệ sử dụng

### Core:
- **Kotlin**: Ngôn ngữ chính
- **Jetpack Compose**: Modern UI
- **Material 3**: Design system

### Architecture:
- **MVVM**: Model-View-ViewModel pattern
- **Repository Pattern**: Single source of truth
- **Dependency Injection**: Hilt

### Networking:
- **Retrofit**: HTTP client
- **OkHttp**: Network layer
- **Gson**: JSON parsing

### Database:
- **Room**: Local database
- **Coroutines**: Asynchronous programming

### Image Loading:
- **Coil**: Image loading library

## 📁 Cấu trúc thư mục

```
app/src/main/java/com/miraimagiclab/novelreadingapp/
├── data/
│   ├── local/                    # Local data source
│   │   ├── dao/                  # Data Access Objects
│   │   ├── database/             # Room database
│   │   ├── entity/               # Database entities
│   │   └── converter/            # Type converters
│   ├── remote/                   # Remote data source
│   │   ├── api/                  # API services
│   │   └── dto/                  # Data Transfer Objects
│   ├── repository/               # Repository implementations
│   └── mapper/                   # Data mappers
├── domain/                       # Domain layer
│   ├── model/                    # Domain models
│   └── repository/               # Repository interfaces
├── di/                          # Dependency injection
│   ├── AppModule.kt
│   ├── DatabaseModule.kt
│   └── NetworkModule.kt
├── ui/                          # Presentation layer
│   ├── screens/                 # UI screens
│   ├── viewmodel/               # ViewModels
│   ├── components/              # Reusable components
│   └── theme/                   # UI theme
├── util/                        # Utilities
│   ├── Constants.kt
│   ├── ErrorHandler.kt
│   ├── UiState.kt
│   └── NetworkResult.kt
└── NovelReadingApplication.kt    # Application class
```

## 🔄 Data Flow

```
User Action → ViewModel → Repository → API/Local DB
     ↓              ↓           ↓
   UI Update ← StateFlow ← Data Mapping
```

1. **User tương tác** với UI
2. **ViewModel** xử lý business logic
3. **Repository** quyết định lấy data từ API hay local DB
4. **Data được map** từ DTO/Entity sang Domain model
5. **StateFlow** emit data mới
6. **UI tự động update** khi nhận được data

## 🎯 Lợi ích của kiến trúc này

### 1. Scalability
- Dễ dàng thêm tính năng mới
- Tách biệt các layer độc lập
- Có thể mở rộng cho nhiều data sources

### 2. Testability
- Unit test cho business logic
- Mock dependencies dễ dàng
- Integration test cho từng layer

### 3. Maintainability
- Code dễ đọc và hiểu
- Separation of concerns rõ ràng
- Dễ dàng debug và fix bugs

### 4. Performance
- Offline-first strategy
- Efficient caching
- Reactive programming với StateFlow

### 5. User Experience
- Smooth UI updates
- Offline capability
- Error handling tốt

## 🚀 Cách chạy dự án

### Yêu cầu:
- Android Studio Arctic Fox+
- Kotlin 1.8.0+
- JDK 11+

### Setup:
1. Clone repository
2. Mở project trong Android Studio
3. Sync Gradle
4. Chạy backend API (port 8080)
5. Run app trên emulator hoặc device

### Backend API:
- Chạy Spring Boot backend trước
- API sẽ chạy tại: `http://localhost:8080/api`
- Emulator sẽ kết nối qua: `http://10.0.2.2:8080/api`

## 📈 Kế hoạch phát triển

### Phase 1 (Current):
- ✅ MVVM architecture setup
- ✅ Novel API integration
- ✅ HomeScreen với real data
- ✅ Offline caching

### Phase 2 (Future):
- [ ] Book details screen
- [ ] Reading functionality
- [ ] User authentication
- [ ] Search và filter
- [ ] User preferences
- [ ] Push notifications

## 🤝 Contributing

1. Fork repository
2. Tạo feature branch
3. Commit changes
4. Push to branch
5. Tạo Pull Request

## 📄 License

Dự án này được phát triển cho mục đích học tập và nghiên cứu.

---

**Tác giả**: Novel Reading App Team  
**Cập nhật lần cuối**: Tháng 10, 2025

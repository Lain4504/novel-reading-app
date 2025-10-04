# Novel Reading App - Simplified Clean Architecture

## 📁 Cấu trúc thư mục (Tối ưu cho API-first app)

```
com.miraimagiclab.novelreadingapp/
├── core/                          # ✅ Shared components
│   ├── data/                      # 🆕 Shared repositories & models
│   │   ├── api/                   # ✅ ApiService
│   │   ├── repository/            # ✅ BookRepository, UserRepository
│   │   ├── model/                 # ✅ Shared Book, User models
│   │   └── di/                    # ✅ DataModule
│   ├── ui/                        # ✅ Common UI components & theme
│   │   ├── components/            # ✅ BottomNavigationBar
│   │   └── theme/                 # ✅ Color, Theme, Typography
│   └── utils/                     # ✅ ApiResult, ApiResponse
├── feature/                       # ✅ Feature modules (simplified)
│   ├── home/                      # ✅ Home feature (CÓ DOMAIN - phức tạp)
│   │   ├── data/                  # ✅ Feature-specific data
│   │   ├── domain/                # ✅ Business logic
│   │   ├── presentation/          # ✅ UI + ViewModel
│   │   └── di/                    # ✅ HomeModule
│   ├── book/                      # ✅ Book feature (CÓ DOMAIN - phức tạp)
│   │   ├── data/
│   │   ├── domain/
│   │   ├── presentation/
│   │   └── di/
│   ├── content/                   # 🆕 Nhóm features đơn giản
│   │   └── presentation/
│   │       ├── explore/           # ✅ ExploreScreen
│   │       ├── search/            # ✅ SearchScreen
│   │       └── library/           # ✅ LibraryScreen
│   └── user/                      # 🆕 Nhóm user features
│       └── presentation/
│           ├── profile/           # ✅ ProfileScreen
│           ├── auth/              # ✅ LoginScreen
│           └── settings/          # ✅ SettingsScreen
├── di/                           # ✅ Global DI modules
└── navigation/                   # ✅ App navigation
```

## 🏗️ Kiến trúc

### **Simplified MVVM + Clean Architecture (API-first)**

#### **Cho features phức tạp (Home, Book):**
```
UI Layer (Compose)
↓
ViewModel (StateFlow + Sealed Classes)
↓  
Use Cases (Business Logic) - Optional
↓
Repository Interface (Domain) - Optional
↓
Shared Repository (Core/Data)
↓
Network Layer (Retrofit + OkHttp)
↓
Backend API
```

#### **Cho features đơn giản (Explore, Search, Profile):**
```
UI Layer (Compose)
↓
ViewModel (StateFlow + Sealed Classes)
↓
Shared Repository (Core/Data)
↓
Network Layer (Retrofit + OkHttp)
↓
Backend API
```

## 📋 Layers

### **1. Presentation Layer**
- **Screens**: Jetpack Compose UI
- **ViewModels**: State management với StateFlow
- **Components**: Reusable UI components

### **2. Domain Layer** (Chỉ cho features phức tạp)
- **Entities**: Business objects
- **Use Cases**: Business logic (nếu cần)
- **Repository Interfaces**: Data access contracts (nếu cần)

### **3. Core/Data Layer** (Shared)
- **Shared Repositories**: BookRepository, UserRepository
- **Shared Models**: Book, User, UserStats
- **API Service**: Retrofit interface
- **Base Repository**: Common error handling

### **4. Core Layer**
- **Utils**: Common utilities, result wrappers
- **UI**: Common UI components & theme
- **DI**: Dependency injection modules

## 🔧 Dependency Injection

- **Hilt**: Global DI framework
- **Feature Modules**: Each feature has its own DI module
- **Repository Binding**: Interface to implementation binding

## 🚀 Benefits

1. **Reduced Boilerplate**: Ít code trùng lặp, dễ maintain
2. **API-first Design**: Tối ưu cho backend xử lý logic nghiệp vụ
3. **Shared Repositories**: Tái sử dụng code, consistency
4. **Flexible Architecture**: Domain layer chỉ khi cần thiết
5. **Team Collaboration**: Nhiều dev có thể làm song song
6. **Easy Testing**: Shared repositories dễ mock và test

## 📝 Naming Conventions

- **Entities**: Domain objects (User, Book, etc.)
- **Models**: Data transfer objects (UserDto, BookDto, etc.)
- **Use Cases**: GetUserStatsUseCase, GetFeaturedBookUseCase
- **Repository**: HomeRepository (interface), HomeRepositoryImpl (implementation)
- **ViewModels**: HomeViewModel
- **Screens**: HomeScreen, BookDetailsScreen

## 🔄 Data Flow

1. **UI** triggers action in **ViewModel**
2. **ViewModel** calls **Use Case**
3. **Use Case** calls **Repository Interface**
4. **Repository Implementation** calls **API Service**
5. **API Service** makes network request
6. **Response** flows back through layers
7. **UI** updates based on **StateFlow** changes

## 🧪 Testing Strategy

- **Unit Tests**: Use cases, repositories, view models
- **Integration Tests**: API service, database
- **UI Tests**: Compose UI testing
- **Mocking**: Use interfaces for easy mocking

## 📱 Feature Development

### **Cho features đơn giản (Explore, Search, Profile):**
1. Tạo screen trong feature/content hoặc feature/user
2. Tạo ViewModel sử dụng shared repositories
3. Cập nhật navigation
4. Done! ✅

### **Cho features phức tạp (Home, Book):**
1. Tạo feature folder structure
2. Define domain entities (nếu cần)
3. Create repository interface (nếu cần)
4. Implement use cases (nếu cần)
5. Build UI screens và ViewModel
6. Add DI module
7. Update navigation

## 🔧 Tools & Libraries

- **Jetpack Compose**: UI framework
- **Hilt**: Dependency injection
- **Retrofit**: Network requests
- **Coroutines**: Asynchronous programming
- **StateFlow**: Reactive state management
- **Navigation Compose**: Navigation
- **Coil**: Image loading

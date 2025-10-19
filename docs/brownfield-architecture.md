# Novel Reading App - Brownfield Architecture Document

## Introduction

This document captures the CURRENT STATE of the Novel Reading App codebase, including technical debt, workarounds, and real-world patterns. It serves as a reference for AI agents working on enhancements and provides a comprehensive understanding of the existing system architecture.

### Document Scope

Comprehensive documentation of entire system including Android frontend, Spring Boot backend, and their integration patterns.

### Change Log

| Date   | Version | Description                 | Author    |
| ------ | ------- | --------------------------- | --------- |
| 2024-12-19 | 1.0     | Initial brownfield analysis | Winston (Architect) |

## Quick Reference - Key Files and Entry Points

### Critical Files for Understanding the System

- **Android Main Entry**: `app/src/main/java/com/miraimagiclab/novelreadingapp/MainActivity.kt`
- **Android Application**: `app/src/main/java/com/miraimagiclab/novelreadingapp/NovelReadingApplication.kt`
- **Backend Main Entry**: `backend/src/main/kotlin/com/miraimagiclab/novelreadingapp/NovelReadingAppApplication.kt`
- **Core Business Logic**: `app/src/main/java/com/miraimagiclab/novelreadingapp/domain/`, `backend/src/main/kotlin/com/miraimagiclab/novelreadingapp/service/`
- **API Definitions**: `backend/src/main/kotlin/com/miraimagiclab/novelreadingapp/controller/`
- **Database Models**: `backend/src/main/kotlin/com/miraimagiclab/novelreadingapp/model/`
- **UI Components**: `app/src/main/java/com/miraimagiclab/novelreadingapp/ui/`

## High Level Architecture

### Technical Summary

The Novel Reading App is a full-stack application consisting of:
- **Android Frontend**: Modern Kotlin-based mobile app using Jetpack Compose
- **Spring Boot Backend**: RESTful API server with MongoDB database
- **Architecture Pattern**: Clean Architecture with MVVM on frontend, MVC on backend

### Actual Tech Stack

| Category  | Technology | Version | Notes                      |
| --------- | ---------- | ------- | -------------------------- |
| **Frontend Runtime** | Android | API 24+ | Min SDK 24, Target SDK 35 |
| **Frontend Language** | Kotlin | 2.0.21 | With Compose compiler |
| **Frontend UI** | Jetpack Compose | 2024.09.00 | Material 3 design system |
| **Frontend DI** | Hilt | 2.48 | Dependency injection |
| **Frontend Database** | Room | 2.6.1 | Local caching |
| **Frontend Networking** | Retrofit | 2.9.0 | REST API client |
| **Frontend Image Loading** | Coil | 2.5.0 | Image loading library |
| **Backend Runtime** | JVM | 17 | Java 17 toolchain |
| **Backend Language** | Kotlin | 2.0.21 | With Spring Boot |
| **Backend Framework** | Spring Boot | 3.3.5 | Web, Security, Data MongoDB |
| **Backend Database** | MongoDB | - | Document database |
| **Backend Security** | JWT | 0.12.6 | JSON Web Tokens |
| **Backend Documentation** | OpenAPI/Swagger | 2.3.0 | API documentation |
| **Backend Storage** | Cloudinary | 1.37.0 | Image/file storage |
| **Build System** | Gradle | - | Multi-module project |

### Repository Structure Reality Check

- **Type**: Multi-module Gradle project
- **Package Manager**: Gradle with version catalogs
- **Notable**: Separate `app` and `backend` modules with shared configuration

## Source Tree and Module Organization

### Project Structure (Actual)

```text
NovelReadingApp/
├── app/                           # Android application module
│   ├── src/main/java/com/miraimagiclab/novelreadingapp/
│   │   ├── data/                  # Data layer (Repository pattern)
│   │   │   ├── auth/              # Authentication data management
│   │   │   ├── local/             # Local data sources (Room, DataStore)
│   │   │   │   ├── dao/           # Room DAOs
│   │   │   │   ├── entity/        # Room entities
│   │   │   │   ├── database/      # Room database setup
│   │   │   │   └── prefs/         # DataStore preferences
│   │   │   ├── mapper/            # Data mapping utilities
│   │   │   ├── remote/            # Remote data sources
│   │   │   │   ├── api/           # Retrofit API services
│   │   │   │   ├── dto/           # Data Transfer Objects
│   │   │   │   └── interceptor/   # Network interceptors
│   │   │   └── repository/        # Repository implementations
│   │   ├── di/                    # Dependency injection modules
│   │   ├── domain/                # Domain layer (Clean Architecture)
│   │   │   ├── model/             # Domain models
│   │   │   └── repository/        # Repository interfaces
│   │   ├── navigation/            # Navigation setup
│   │   ├── ui/                    # Presentation layer
│   │   │   ├── components/        # Reusable UI components
│   │   │   ├── screens/           # Screen composables
│   │   │   │   └── auth/          # Authentication screens
│   │   │   ├── theme/             # Material 3 theming
│   │   │   └── viewmodel/         # ViewModels
│   │   ├── util/                  # Utility classes
│   │   ├── MainActivity.kt        # Main activity
│   │   └── NovelReadingApplication.kt
├── backend/                       # Spring Boot backend module
│   └── src/main/kotlin/com/miraimagiclab/novelreadingapp/
│       ├── config/                # Configuration classes
│       ├── controller/            # REST controllers
│       ├── dto/                   # Data Transfer Objects
│       │   ├── request/           # Request DTOs
│       │   └── response/          # Response DTOs
│       ├── enumeration/           # Enums
│       ├── exception/             # Exception handling
│       ├── model/                 # MongoDB document models
│       ├── repository/            # Spring Data repositories
│       ├── service/               # Business logic services
│       └── NovelReadingAppApplication.kt
└── build.gradle.kts               # Root build configuration
```

### Key Modules and Their Purpose

- **Authentication System**: `app/data/auth/SessionManager.kt` - JWT token management
- **Novel Management**: `backend/service/NovelService.kt` - Core business logic for novels
- **User Interface**: `app/ui/screens/` - Jetpack Compose screens
- **Data Persistence**: `app/data/local/` - Room database and DataStore
- **API Communication**: `app/data/remote/api/` - Retrofit services
- **Dependency Injection**: `app/di/` - Hilt modules for DI setup

## Data Models and APIs

### Data Models

#### Frontend Domain Models
- **Novel Model**: See `app/src/main/java/com/miraimagiclab/novelreadingapp/domain/model/Novel.kt`
- **Chapter Model**: See `app/src/main/java/com/miraimagiclab/novelreadingapp/domain/model/Chapter.kt`
- **User Models**: Authentication and profile models in domain layer

#### Backend Document Models
- **Novel Document**: See `backend/src/main/kotlin/com/miraimagiclab/novelreadingapp/model/Novel.kt`
- **User Document**: See `backend/src/main/kotlin/com/miraimagiclab/novelreadingapp/model/User.kt`
- **Chapter Document**: See `backend/src/main/kotlin/com/miraimagiclab/novelreadingapp/model/Chapter.kt`

### API Specifications

- **OpenAPI Spec**: Available at `/swagger-ui.html` when backend is running
- **Main Endpoints**:
  - `/novels` - Novel CRUD operations
  - `/auth` - Authentication endpoints
  - `/users` - User management
  - `/chapters` - Chapter management
  - `/reviews` - Review system
  - `/comments` - Comment system

## Technical Debt and Known Issues

### Critical Technical Debt

1. **Authentication Token Management**: 
   - Token refresh logic is complex and scattered across multiple classes
   - Session management in `SessionManager.kt` could be simplified
   - JWT token validation happens in multiple places

2. **Data Synchronization**: 
   - No clear offline-first strategy
   - Local cache refresh logic is inconsistent across repositories
   - Network error handling is basic (catch-all exceptions)

3. **UI State Management**: 
   - Some ViewModels have complex state management
   - Loading states are not consistently handled across screens
   - Error states could be more user-friendly

4. **Backend Validation**: 
   - Some controllers have extensive parameter validation that could be extracted
   - File upload handling is mixed with business logic

### Workarounds and Gotchas

- **Network Configuration**: `android:usesCleartextTraffic="true"` in manifest for development
- **Database Migrations**: Room database migrations are not yet implemented
- **Image Upload**: Cloudinary integration requires specific folder structure
- **CORS Configuration**: Backend has specific CORS origins configured for development
- **Token Refresh**: Automatic token refresh happens on app startup with 60-minute threshold

## Integration Points and External Dependencies

### External Services

| Service  | Purpose  | Integration Type | Key Files                      |
| -------- | -------- | ---------------- | ------------------------------ |
| Cloudinary | Image Storage | REST API         | `backend/service/CloudinaryStorageService.kt` |
| MongoDB | Database | Spring Data | `backend/repository/` |
| Email Service | Notifications | SMTP | `backend/service/EmailService.kt` |

### Internal Integration Points

- **Frontend-Backend Communication**: REST API on configurable port, JWT authentication
- **Local Data Storage**: Room database for caching, DataStore for preferences
- **Image Loading**: Coil library with Cloudinary URLs
- **Navigation**: Jetpack Navigation with type-safe arguments

## Development and Deployment

### Local Development Setup

#### Android Frontend
1. Android Studio with Kotlin 2.0.21
2. Android SDK 24+ (API level 24 minimum)
3. Gradle 8.10.0
4. Hilt for dependency injection

#### Backend
1. Java 17 JDK
2. MongoDB instance (local or cloud)
3. Cloudinary account for image storage
4. SMTP configuration for email service

### Build and Deployment Process

- **Android Build**: `./gradlew assembleDebug` or `./gradlew assembleRelease`
- **Backend Build**: `./gradlew :backend:bootJar`
- **Backend Run**: `./gradlew :backend:bootRun` or use provided scripts
- **Environments**: Development (localhost), Production (TBD)

## Testing Reality

### Current Test Coverage

- **Unit Tests**: Minimal coverage in both frontend and backend
- **Integration Tests**: Backend has TestContainers setup for MongoDB
- **UI Tests**: No automated UI tests implemented
- **Manual Testing**: Primary QA method

### Running Tests

```bash
# Frontend tests
./gradlew :app:test

# Backend tests  
./gradlew :backend:test

# Integration tests (requires Docker)
./gradlew :backend:integrationTest
```

## Architecture Patterns and Conventions

### Frontend Patterns

1. **Clean Architecture**: Domain, Data, and Presentation layers
2. **MVVM**: ViewModels with Jetpack Compose UI
3. **Repository Pattern**: Data abstraction layer
4. **Dependency Injection**: Hilt for DI
5. **Reactive Programming**: Kotlin Flows for data streams

### Backend Patterns

1. **MVC**: Controllers, Services, Repositories
2. **Spring Data**: Repository abstraction for MongoDB
3. **DTO Pattern**: Separate request/response objects
4. **Exception Handling**: Global exception handler
5. **Validation**: Bean validation with custom messages

### Code Conventions

- **Package Structure**: Feature-based organization
- **Naming**: Kotlin conventions (camelCase, descriptive names)
- **Documentation**: KDoc for public APIs
- **Error Handling**: Result types and exception handling
- **Logging**: Android Log for frontend, SLF4J for backend

## Security Implementation

### Authentication & Authorization

- **JWT Tokens**: Access and refresh token pattern
- **Token Storage**: Encrypted SharedPreferences via DataStore
- **API Security**: Spring Security with JWT filter
- **Password Security**: BCrypt hashing (implied in Spring Security)

### Data Protection

- **Network Security**: HTTPS in production (cleartext for dev)
- **Local Storage**: Room database encryption available
- **Image Security**: Cloudinary signed URLs
- **Input Validation**: Backend validation on all endpoints

## Performance Considerations

### Frontend Performance

- **Image Loading**: Coil with caching and resizing
- **Database**: Room with proper indexing
- **Memory**: Compose recomposition optimization
- **Network**: Retrofit with OkHttp caching

### Backend Performance

- **Database**: MongoDB with proper indexing
- **Caching**: No explicit caching layer implemented
- **File Storage**: Cloudinary CDN for images
- **API Response**: Pagination for large datasets

## Appendix - Useful Commands and Scripts

### Frequently Used Commands

```bash
# Build entire project
./gradlew build

# Run Android app
./gradlew :app:installDebug

# Run backend
./gradlew :backend:bootRun

# Clean build
./gradlew clean

# Run tests
./gradlew test
```

### Debugging and Troubleshooting

- **Android Logs**: Use `adb logcat` for Android debugging
- **Backend Logs**: Check console output or log files
- **Database**: MongoDB Compass for database inspection
- **Network**: Use Retrofit logging interceptor for API debugging
- **Common Issues**: 
  - Token expiration (check SessionManager)
  - Network connectivity (verify backend is running)
  - Image loading (check Cloudinary configuration)

## Future Enhancement Considerations

### Scalability Improvements

1. **Caching Strategy**: Implement Redis for backend caching
2. **Database Optimization**: Add proper indexing and query optimization
3. **Offline Support**: Implement robust offline-first architecture
4. **Performance Monitoring**: Add APM tools for both frontend and backend

### Technical Debt Resolution

1. **Authentication Refactor**: Simplify token management
2. **Error Handling**: Implement comprehensive error handling strategy
3. **Testing**: Increase test coverage across all layers
4. **Documentation**: Add comprehensive API documentation

### Feature Enhancements

1. **Real-time Features**: WebSocket support for live updates
2. **Advanced Search**: Elasticsearch integration
3. **Recommendation Engine**: ML-based novel recommendations
4. **Social Features**: Enhanced user interaction capabilities

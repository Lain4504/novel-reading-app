# Source Tree and Module Organization

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

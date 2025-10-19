# Development and Deployment

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

# High Level Architecture

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

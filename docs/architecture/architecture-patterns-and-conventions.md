# Architecture Patterns and Conventions

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

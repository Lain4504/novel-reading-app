# Data Models and APIs

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

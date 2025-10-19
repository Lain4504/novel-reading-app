# Security Implementation

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

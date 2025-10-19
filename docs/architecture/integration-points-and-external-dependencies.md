# Integration Points and External Dependencies

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

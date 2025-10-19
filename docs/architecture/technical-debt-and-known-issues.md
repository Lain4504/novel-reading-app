# Technical Debt and Known Issues

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

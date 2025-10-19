# Appendix - Useful Commands and Scripts

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

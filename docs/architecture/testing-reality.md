# Testing Reality

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

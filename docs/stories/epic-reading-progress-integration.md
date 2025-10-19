# Reading Progress Integration - Brownfield Enhancement

## Epic Goal

Replace the current mock reading progress implementation with proper backend integration to track which chapter the user is currently reading, leveraging the existing UserNovelInteraction API.

## Epic Description

**Existing System Context:**

- Current relevant functionality: ReadingScreen with complex character-level progress tracking using mock data
- Technology stack: Android Compose, Hilt DI, Kotlin, Spring Boot backend with MongoDB
- Integration points: UserNovelInteractionController API, ReadingProgressViewModel, ReadingProgressRepository

**Enhancement Details:**

- What's being added/changed: Replace mock ReadingProgressRepository with real API integration to track chapter-level reading progress
- How it integrates: Use existing `/interactions/users/{userId}/novels/{novelId}/read` endpoint to update reading progress
- Success criteria: User's current chapter is properly tracked and persisted in backend, reading progress is restored when returning to novels

## Stories

1. **Story 1:** Replace ReadingProgressRepository with API integration
   - Remove mock implementation
   - Create API service to call backend reading progress endpoint
   - Update repository to use real API calls

2. **Story 2:** Simplify ReadingProgress model to match backend capabilities
   - Remove character-level tracking (position, totalCharacters, readingTimeSeconds)
   - Keep only chapter-level tracking (currentChapterId, lastReadAt)
   - Update ReadingProgressViewModel to work with simplified model

3. **Story 3:** Update ReadingScreen to use simplified progress tracking
   - Remove character position tracking from scroll events
   - Update progress indicator to show chapter completion status
   - Ensure proper API calls when navigating between chapters

## Compatibility Requirements

- [ ] Existing APIs remain unchanged
- [ ] Database schema changes are backward compatible (using existing UserNovelInteraction collection)
- [ ] UI changes follow existing patterns (minimal changes to ReadingScreen)
- [ ] Performance impact is minimal (reduced complexity actually improves performance)

## Risk Mitigation

- **Primary Risk:** Loss of detailed reading progress data (character position, reading time)
- **Mitigation:** Focus on core user need - knowing which chapter they're on. Detailed tracking can be added later if needed
- **Rollback Plan:** Keep existing mock implementation as fallback, can easily revert to previous state

## Definition of Done

- [ ] All stories completed with acceptance criteria met
- [ ] Reading progress properly synced with backend UserNovelInteraction API
- [ ] User can resume reading from last chapter when returning to novel
- [ ] No regression in existing reading functionality
- [ ] Mock data completely removed from reading progress implementation
- [ ] API integration tested and working correctly

## Technical Implementation Notes

**Backend API Integration:**
- Endpoint: `POST /interactions/users/{userId}/novels/{novelId}/read?chapterNumber={chapterNumber}&chapterId={chapterId}`
- Response: `UserNovelInteractionDto` with updated `currentChapterNumber`, `currentChapterId`, `lastReadAt`

**Simplified ReadingProgress Model:**
```kotlin
data class ReadingProgress(
    val userId: String,
    val novelId: String,
    val currentChapterId: String?,
    val currentChapterNumber: Int?,
    val lastReadAt: Date?
)
```

**Key Changes:**
1. Remove character-level tracking complexity
2. Focus on chapter-level progress only
3. Use existing backend API instead of mock data
4. Maintain user experience while simplifying implementation

## Success Metrics

- Reading progress is correctly saved to backend when user navigates chapters
- User can resume reading from correct chapter when returning to novel
- No performance degradation in reading experience
- Reduced code complexity and maintenance burden

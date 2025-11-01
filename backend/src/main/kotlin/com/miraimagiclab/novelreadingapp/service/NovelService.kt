package com.miraimagiclab.novelreadingapp.service

import com.miraimagiclab.novelreadingapp.dto.request.NovelCreateRequest
import com.miraimagiclab.novelreadingapp.dto.request.NovelSearchRequest
import com.miraimagiclab.novelreadingapp.dto.request.NovelUpdateRequest
import com.miraimagiclab.novelreadingapp.dto.response.NovelDto
import com.miraimagiclab.novelreadingapp.dto.response.PageResponse
import com.miraimagiclab.novelreadingapp.exception.DuplicateNovelException
import com.miraimagiclab.novelreadingapp.exception.NovelNotFoundException
import com.miraimagiclab.novelreadingapp.model.Novel
import com.miraimagiclab.novelreadingapp.repository.NovelRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class NovelService(
    private val novelRepository: NovelRepository,
    private val reviewService: ReviewService,
    private val userNovelInteractionService: UserNovelInteractionService
) {

    fun createNovel(request: NovelCreateRequest): NovelDto {
        // Check for duplicate novel
        if (novelRepository.existsByTitleAndAuthorName(request.title, request.authorName)) {
            throw DuplicateNovelException("A novel with title '${request.title}' by author '${request.authorName}' already exists")
        }

        val novel = Novel(
            title = request.title,
            description = request.description,
            authorName = request.authorName,
            coverImage = request.coverImage,
            categories = request.categories,
            rating = 0.0,
            wordCount = 0,
            chapterCount = 0,
            authorId = request.authorId,
            status = request.status,
            isR18 = request.isR18,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val savedNovel = novelRepository.save(novel)
        return NovelDto.fromEntity(savedNovel)
    }

    @Transactional(readOnly = true)
    fun getNovelById(id: String): NovelDto {
        val novel = novelRepository.findById(id)
            .orElseThrow { NovelNotFoundException("Novel with ID '$id' not found") }
        return NovelDto.fromEntity(novel)
    }

    fun updateNovel(id: String, request: NovelUpdateRequest): NovelDto {
        val existingNovel = novelRepository.findById(id)
            .orElseThrow { NovelNotFoundException("Novel with ID '$id' not found") }

        // Check for duplicate if title or author name is being updated
        if (request.title != null && request.authorName != null) {
            if (novelRepository.existsByTitleAndAuthorName(request.title, request.authorName) &&
                (existingNovel.title != request.title || existingNovel.authorName != request.authorName)) {
                throw DuplicateNovelException("A novel with title '${request.title}' by author '${request.authorName}' already exists")
            }
        }

        val updatedNovel = existingNovel.copy(
            title = request.title ?: existingNovel.title,
            description = request.description ?: existingNovel.description,
            authorName = request.authorName ?: existingNovel.authorName,
            coverImage = request.coverImage ?: existingNovel.coverImage,
            categories = request.categories ?: existingNovel.categories,
            rating = request.rating ?: existingNovel.rating,
            wordCount = request.wordCount ?: existingNovel.wordCount,
            chapterCount = request.chapterCount ?: existingNovel.chapterCount,
            authorId = request.authorId ?: existingNovel.authorId,
            status = request.status ?: existingNovel.status,
            isR18 = request.isR18 ?: existingNovel.isR18,
            updatedAt = LocalDateTime.now()
        )

        val savedNovel = novelRepository.save(updatedNovel)
        return NovelDto.fromEntity(savedNovel)
    }

    fun deleteNovel(id: String) {
        if (!novelRepository.existsById(id)) {
            throw NovelNotFoundException("Novel with ID '$id' not found")
        }
        novelRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    fun searchNovels(request: NovelSearchRequest): PageResponse<NovelDto> {
        val sort = Sort.by(
            if (request.sortDirection == "asc") Sort.Direction.ASC else Sort.Direction.DESC,
            request.sortBy
        )
        val pageable: Pageable = PageRequest.of(request.page, request.size, sort)

        // Use simple repository methods when possible for better performance
        val novels = when {
            // Only title search
            request.title != null && request.authorName == null && request.categories == null &&
            request.status == null && request.minRating == null && request.maxRating == null && request.isR18 == null -> {
                novelRepository.findByTitleContainingIgnoreCase(request.title, pageable)
            }
            // Only author search
            request.title == null && request.authorName != null && request.categories == null &&
            request.status == null && request.minRating == null && request.maxRating == null && request.isR18 == null -> {
                novelRepository.findByAuthorNameContainingIgnoreCase(request.authorName, pageable)
            }
            // No filters - get all
            request.title == null && request.authorName == null && request.categories == null &&
            request.status == null && request.minRating == null && request.maxRating == null && request.isR18 == null -> {
                novelRepository.findAll(pageable)
            }
            // Complex search with multiple criteria
            else -> {
                novelRepository.findByMultipleCriteria(
                    title = request.title,
                    authorName = request.authorName,
                    categories = request.categories,
                    status = request.status,
                    minRating = request.minRating,
                    maxRating = request.maxRating,
                    isR18 = request.isR18,
                    pageable = pageable
                )
            }
        }

        val novelDtos = novels.content.map { NovelDto.fromEntity(it) }
        return PageResponse(
            content = novelDtos,
            page = novels.number,
            size = novels.size,
            totalElements = novels.totalElements,
            totalPages = novels.totalPages,
            first = novels.isFirst,
            last = novels.isLast,
            numberOfElements = novels.numberOfElements
        )
    }

    @Transactional(readOnly = true)
    fun getAllNovels(page: Int = 0, size: Int = 20): PageResponse<NovelDto> {
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val novels = novelRepository.findAll(pageable)
        
        val novelDtos = novels.content.map { NovelDto.fromEntity(it) }
        return PageResponse(
            content = novelDtos,
            page = novels.number,
            size = novels.size,
            totalElements = novels.totalElements,
            totalPages = novels.totalPages,
            first = novels.isFirst,
            last = novels.isLast,
            numberOfElements = novels.numberOfElements
        )
    }

    @Transactional(readOnly = true)
    fun getNovelsByAuthor(authorId: String, page: Int = 0, size: Int = 20): PageResponse<NovelDto> {
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val novels = novelRepository.findByAuthorId(authorId, pageable)
        
        val novelDtos = novels.content.map { NovelDto.fromEntity(it) }
        return PageResponse(
            content = novelDtos,
            page = novels.number,
            size = novels.size,
            totalElements = novels.totalElements,
            totalPages = novels.totalPages,
            first = novels.isFirst,
            last = novels.isLast,
            numberOfElements = novels.numberOfElements
        )
    }

    @Transactional(readOnly = true)
    fun getTopNovelsByViewCount(): List<NovelDto> {
        return novelRepository.findTop10ByOrderByViewCountDesc().map { NovelDto.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun getTopNovelsByFollowCount(): List<NovelDto> {
        return novelRepository.findTop10ByOrderByFollowCountDesc().map { NovelDto.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun getTopNovelsByRating(): List<NovelDto> {
        return novelRepository.findTop10ByOrderByRatingDesc().map { NovelDto.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun getRecentlyUpdatedNovels(): List<NovelDto> {
        return novelRepository.findTop10ByOrderByUpdatedAtDesc().map { NovelDto.fromEntity(it) }
    }

    fun incrementViewCount(id: String): NovelDto {
        val novel = novelRepository.findById(id)
            .orElseThrow { NovelNotFoundException("Novel with ID '$id' not found") }

        val updatedNovel = novel.copy(
            viewCount = novel.viewCount + 1,
            updatedAt = LocalDateTime.now()
        )

        val savedNovel = novelRepository.save(updatedNovel)
        return NovelDto.fromEntity(savedNovel)
    }

    fun toggleFollow(id: String, userId: String): NovelDto {
        val novel = novelRepository.findById(id)
            .orElseThrow { NovelNotFoundException("Novel with ID '$id' not found") }

        val interaction = userNovelInteractionService.toggleFollow(userId, id)

        // Update follow count in novel
        val followCount = userNovelInteractionService.getFollowCount(id)
        val updatedNovel = novel.copy(
            followCount = followCount.toInt(),
            updatedAt = LocalDateTime.now()
        )

        val savedNovel = novelRepository.save(updatedNovel)
        return NovelDto.fromEntity(savedNovel)
    }

    fun seedTestData() {
        val testNovels = listOf(
            com.miraimagiclab.novelreadingapp.model.Novel(
                title = "Test Novel 1",
                description = "This is a test novel for development",
                authorName = "Test Author 1",
                coverImage = "https://example.com/cover1.jpg",
                categories = setOf(com.miraimagiclab.novelreadingapp.enumeration.CategoryEnum.FANTASY),
                rating = 4.5,
                wordCount = 50000,
                chapterCount = 10,
                viewCount = 100,
                followCount = 20,
                status = com.miraimagiclab.novelreadingapp.enumeration.NovelStatusEnum.ONGOING,
                isR18 = false
            ),
            com.miraimagiclab.novelreadingapp.model.Novel(
                title = "Test Novel 2",
                description = "Another test novel for development",
                authorName = "Test Author 2",
                coverImage = "https://example.com/cover2.jpg",
                categories = setOf(com.miraimagiclab.novelreadingapp.enumeration.CategoryEnum.ROMANCE),
                rating = 4.2,
                wordCount = 75000,
                chapterCount = 15,
                viewCount = 200,
                followCount = 35,
                status = com.miraimagiclab.novelreadingapp.enumeration.NovelStatusEnum.COMPLETED,
                isR18 = false
            ),
            com.miraimagiclab.novelreadingapp.model.Novel(
                title = "Test Novel 3",
                description = "Third test novel for development",
                authorName = "Test Author 3",
                coverImage = "https://example.com/cover3.jpg",
                categories = setOf(com.miraimagiclab.novelreadingapp.enumeration.CategoryEnum.ACTION),
                rating = 4.8,
                wordCount = 100000,
                chapterCount = 20,
                viewCount = 300,
                followCount = 50,
                status = com.miraimagiclab.novelreadingapp.enumeration.NovelStatusEnum.ONGOING,
                isR18 = false
            )
        )
        
        novelRepository.saveAll(testNovels)
    }

    fun unfollow(id: String, userId: String): NovelDto {
        val novel = novelRepository.findById(id)
            .orElseThrow { NovelNotFoundException("Novel with ID '$id' not found") }

        // For unfollow, we need to set hasFollowing to false
        val updateRequest = com.miraimagiclab.novelreadingapp.dto.request.UserNovelInteractionUpdateRequest(
            hasFollowing = false
        )
        userNovelInteractionService.createOrUpdateInteraction(userId, id, updateRequest)

        // Update follow count in novel
        val followCount = userNovelInteractionService.getFollowCount(id)
        val updatedNovel = novel.copy(
            followCount = followCount.toInt(),
            updatedAt = LocalDateTime.now()
        )

        val savedNovel = novelRepository.save(updatedNovel)
        return NovelDto.fromEntity(savedNovel)
    }

    fun addOrUpdateRating(id: String, userId: String, rating: Double): NovelDto {
        val novel = novelRepository.findById(id)
            .orElseThrow { NovelNotFoundException("Novel with ID '$id' not found") }

        // This would typically create/update a review, but for now we'll just update the novel's rating
        // In a real implementation, you'd want to calculate average from reviews
        val currentRatingCount = novel.ratingCount + 1
        val newRating = ((novel.rating * novel.ratingCount) + rating) / currentRatingCount

        val updatedNovel = novel.copy(
            rating = newRating,
            ratingCount = currentRatingCount,
            updatedAt = LocalDateTime.now()
        )

        val savedNovel = novelRepository.save(updatedNovel)
        return NovelDto.fromEntity(savedNovel)
    }
}

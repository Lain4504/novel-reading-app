package com.miraimagiclab.novelreadingapp.service

import com.miraimagiclab.novelreadingapp.dto.request.UserNovelInteractionUpdateRequest
import com.miraimagiclab.novelreadingapp.dto.response.UserNovelInteractionDto
import com.miraimagiclab.novelreadingapp.exception.UserNotFoundException
import com.miraimagiclab.novelreadingapp.model.UserNovelInteraction
import com.miraimagiclab.novelreadingapp.repository.UserNovelInteractionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class UserNovelInteractionService(
    private val userNovelInteractionRepository: UserNovelInteractionRepository
) {

    @Transactional(readOnly = true)
    fun getUserNovelInteraction(userId: String, novelId: String): UserNovelInteractionDto? {
        val interaction = userNovelInteractionRepository.findByUserIdAndNovelId(userId, novelId)
        return interaction.map { UserNovelInteractionDto.fromEntity(it) }.orElse(null)
    }

    @Transactional(readOnly = true)
    fun getUserInteractions(userId: String): List<UserNovelInteractionDto> {
        return userNovelInteractionRepository.findByUserId(userId)
            .map { UserNovelInteractionDto.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun getNovelInteractions(novelId: String): List<UserNovelInteractionDto> {
        return userNovelInteractionRepository.findByNovelId(novelId)
            .map { UserNovelInteractionDto.fromEntity(it) }
    }

    fun createOrUpdateInteraction(userId: String, novelId: String, request: UserNovelInteractionUpdateRequest): UserNovelInteractionDto {
        val existingInteraction = userNovelInteractionRepository.findByUserIdAndNovelId(userId, novelId)

        val interaction = if (existingInteraction.isPresent) {
            // Update existing interaction
            val current = existingInteraction.get()
            current.copy(
                hasFollowing = request.hasFollowing ?: current.hasFollowing,
                inWishlist = request.inWishlist ?: current.inWishlist,
                notify = request.notify ?: current.notify,
                currentChapterNumber = request.currentChapterNumber ?: current.currentChapterNumber,
                currentChapterId = request.currentChapterId ?: current.currentChapterId,
                lastReadAt = request.lastReadAt?.let { LocalDateTime.parse(it) } ?: current.lastReadAt,
                totalChapterReads = request.totalChapterReads ?: current.totalChapterReads,
                updatedAt = LocalDateTime.now()
            )
        } else {
            // Create new interaction
            UserNovelInteraction(
                userId = userId,
                novelId = novelId,
                hasFollowing = request.hasFollowing ?: false,
                inWishlist = request.inWishlist ?: false,
                notify = request.notify ?: false,
                currentChapterNumber = request.currentChapterNumber,
                currentChapterId = request.currentChapterId,
                lastReadAt = request.lastReadAt?.let { LocalDateTime.parse(it) },
                totalChapterReads = request.totalChapterReads ?: 0,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        }

        val savedInteraction = userNovelInteractionRepository.save(interaction)
        return UserNovelInteractionDto.fromEntity(savedInteraction)
    }

    fun toggleFollow(userId: String, novelId: String): UserNovelInteractionDto {
        val existingInteraction = userNovelInteractionRepository.findByUserIdAndNovelId(userId, novelId)

        val interaction = if (existingInteraction.isPresent) {
            val current = existingInteraction.get()
            current.copy(
                hasFollowing = !current.hasFollowing,
                updatedAt = LocalDateTime.now()
            )
        } else {
            UserNovelInteraction(
                userId = userId,
                novelId = novelId,
                hasFollowing = true,
                inWishlist = false,
                notify = false,
                totalChapterReads = 0,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        }

        val savedInteraction = userNovelInteractionRepository.save(interaction)
        return UserNovelInteractionDto.fromEntity(savedInteraction)
    }

    fun toggleWishlist(userId: String, novelId: String): UserNovelInteractionDto {
        val existingInteraction = userNovelInteractionRepository.findByUserIdAndNovelId(userId, novelId)

        val interaction = if (existingInteraction.isPresent) {
            val current = existingInteraction.get()
            current.copy(
                inWishlist = !current.inWishlist,
                updatedAt = LocalDateTime.now()
            )
        } else {
            UserNovelInteraction(
                userId = userId,
                novelId = novelId,
                hasFollowing = false,
                inWishlist = true,
                notify = false,
                totalChapterReads = 0,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        }

        val savedInteraction = userNovelInteractionRepository.save(interaction)
        return UserNovelInteractionDto.fromEntity(savedInteraction)
    }

    fun updateReadingProgress(userId: String, novelId: String, chapterNumber: Int, chapterId: String): UserNovelInteractionDto {
        val existingInteraction = userNovelInteractionRepository.findByUserIdAndNovelId(userId, novelId)

        val interaction = if (existingInteraction.isPresent) {
            val current = existingInteraction.get()
            current.copy(
                currentChapterNumber = chapterNumber,
                currentChapterId = chapterId,
                lastReadAt = LocalDateTime.now(),
                totalChapterReads = current.totalChapterReads + 1,
                updatedAt = LocalDateTime.now()
            )
        } else {
            UserNovelInteraction(
                userId = userId,
                novelId = novelId,
                hasFollowing = false,
                inWishlist = false,
                notify = false,
                currentChapterNumber = chapterNumber,
                currentChapterId = chapterId,
                lastReadAt = LocalDateTime.now(),
                totalChapterReads = 1,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        }

        val savedInteraction = userNovelInteractionRepository.save(interaction)
        return UserNovelInteractionDto.fromEntity(savedInteraction)
    }

    fun getFollowCount(novelId: String): Long {
        return userNovelInteractionRepository.countByNovelIdAndHasFollowing(novelId, true)
    }

    fun getUserFollowingList(userId: String): List<UserNovelInteractionDto> {
        return userNovelInteractionRepository.findByUserIdAndHasFollowing(userId, true)
            .map { UserNovelInteractionDto.fromEntity(it) }
    }

    fun getUserWishlist(userId: String): List<UserNovelInteractionDto> {
        return userNovelInteractionRepository.findByUserIdAndInWishlist(userId, true)
            .map { UserNovelInteractionDto.fromEntity(it) }
    }
}
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
    private val novelRepository: NovelRepository
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

        val novels = novelRepository.findByMultipleCriteria(
            title = request.title,
            authorName = request.authorName,
            categories = request.categories,
            status = request.status,
            minRating = request.minRating,
            maxRating = request.maxRating,
            isR18 = request.isR18,
            pageable = pageable
        )

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
}

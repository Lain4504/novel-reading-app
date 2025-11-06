package com.miraimagiclab.novelreadingapp.service

import com.miraimagiclab.novelreadingapp.dto.request.ChapterCreateRequest
import com.miraimagiclab.novelreadingapp.dto.request.ChapterUpdateRequest
import com.miraimagiclab.novelreadingapp.dto.response.ChapterResponseDto
import com.miraimagiclab.novelreadingapp.dto.response.PageResponse
import com.miraimagiclab.novelreadingapp.model.Chapter
import com.miraimagiclab.novelreadingapp.repository.ChapterRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PathVariable

@Service
@Transactional
class ChapterService (
    private val chapterRepository: ChapterRepository,
    private val novelService: NovelService,
    private val userNovelInteractionService: UserNovelInteractionService,
    private val deviceTokenService: DeviceTokenService,
    private val fcmService: FcmService
){
    fun createChapter(novelId: String, request: ChapterCreateRequest): ChapterResponseDto {
        val logger = org.slf4j.LoggerFactory.getLogger(ChapterService::class.java)
        logger.info("=== ChapterService: Creating new chapter ===")
        logger.info("NovelId: $novelId, ChapterTitle: ${request.chapterTitle}")
        
        val chapter = Chapter(
            novelId = novelId,
            chapterTitle = request.chapterTitle,
            content = request.content,
            wordCount = request.content.split("\\s+".toRegex()).size,
            viewCount = 0,
            chapterNumber = chapterRepository.countByNovelId(novelId) + 1
        )
        logger.info("Chapter number calculated: ${chapter.chapterNumber}")
        
        val savedChapter = chapterRepository.save(chapter)
        logger.info("Chapter saved with ID: ${savedChapter.id}")
        
        // Update novel stats (chapterCount, wordCount) from all chapters
        try {
            novelService.updateNovelStatsFromChapters(novelId)
            logger.info("Novel stats updated successfully for novel $novelId")
        } catch (e: Exception) {
            logger.error("Failed to update novel stats: ${e.message}", e)
            // Don't fail chapter creation if stats update fails
        }
        
        // Send push notifications to users following this novel
        logger.info("Triggering notification sending for chapter ${savedChapter.id}")
        sendChapterUpdateNotifications(novelId, savedChapter)
        
        logger.info("=== ChapterService: Chapter creation completed ===")
        return ChapterResponseDto(
            id = savedChapter.id!!,
            novelId = savedChapter.novelId,
            chapterTitle = savedChapter.chapterTitle,
            chapterNumber = savedChapter.chapterNumber,
            content = savedChapter.content,
            wordCount = savedChapter.wordCount,
            viewCount = savedChapter.viewCount,
            createdAt = savedChapter.createdAt.toString(),
            updatedAt = savedChapter.updatedAt.toString()
        )
    }
    
    private fun sendChapterUpdateNotifications(novelId: String, chapter: Chapter) {
        val logger = org.slf4j.LoggerFactory.getLogger(ChapterService::class.java)
        
        try {
            logger.info("=== START: Sending chapter update notifications ===")
            logger.info("NovelId: $novelId, ChapterId: ${chapter.id}, ChapterNumber: ${chapter.chapterNumber}, ChapterTitle: ${chapter.chapterTitle}")
            
            // Get all users following this novel with notifications enabled
            val allInteractions = userNovelInteractionService.getNovelInteractions(novelId)
            logger.info("Total interactions for novel $novelId: ${allInteractions.size}")
            
            val interactions = allInteractions.filter { it.hasFollowing && it.notify }
            logger.info("Interactions with hasFollowing=true and notify=true: ${interactions.size}")
            
            if (interactions.isEmpty()) {
                logger.warn("No users found following this novel with notifications enabled. Skipping notifications.")
                return
            }
            
            // Log each interaction
            interactions.forEach { interaction ->
                logger.debug("User ${interaction.userId}: following=${interaction.hasFollowing}, notify=${interaction.notify}, currentChapter=${interaction.currentChapterNumber}")
            }
            
            // Get novel title
            val novel = try {
                novelService.getNovelById(novelId)
            } catch (e: Exception) {
                logger.warn("Failed to get novel details: ${e.message}")
                null
            }
            val novelTitle = novel?.title ?: "Truyện bạn theo dõi"
            logger.info("Novel title: $novelTitle")
            
            // Get user IDs
            val userIds = interactions.map { it.userId }.distinct()
            logger.info("Unique user IDs to notify: ${userIds.size} - $userIds")
            
            // Get FCM tokens for these users
            val tokensByUserId = deviceTokenService.getTokensByUserIds(userIds)
            logger.info("Total users with tokens: ${tokensByUserId.size}")
            
            var totalTokens = 0
            tokensByUserId.forEach { (userId, tokens) ->
                logger.info("User $userId has ${tokens.size} token(s)")
                totalTokens += tokens.size
                tokens.forEach { token ->
                    logger.debug("User $userId token: ${token.take(20)}...")
                }
            }
            
            if (totalTokens == 0) {
                logger.warn("No FCM tokens found for any users. Notifications cannot be sent.")
                return
            }
            
            // Prepare notification content
            val title = "$novelTitle có chương mới"
            val body = "Chương ${chapter.chapterNumber}: ${chapter.chapterTitle}"
            val data = mapOf(
                "type" to "NEW_CHAPTER",
                "novelId" to novelId,
                "chapterId" to (chapter.id ?: ""),
                "chapterNumber" to chapter.chapterNumber.toString()
            )
            
            logger.info("Notification content - Title: $title, Body: $body")
            logger.info("Notification data: $data")
            
            // Send notifications to all tokens
            var notificationsSent = 0
            var notificationsSkipped = 0
            
            tokensByUserId.forEach { (userId, tokens) ->
                // Get user's current chapter number to check if this is a new chapter
                val interaction = interactions.find { it.userId == userId }
                val currentChapterNumber = interaction?.currentChapterNumber ?: 0
                
                logger.info("User $userId: currentChapter=$currentChapterNumber, newChapter=${chapter.chapterNumber}")
                
                if (chapter.chapterNumber > currentChapterNumber) {
                    tokens.forEach { token ->
                        logger.info("Sending notification to user $userId, token: ${token.take(20)}...")
                        val success = fcmService.sendNotification(token, title, body, data)
                        if (success) {
                            notificationsSent++
                            logger.info("✓ Notification sent successfully to user $userId")
                        } else {
                            logger.error("✗ Failed to send notification to user $userId")
                        }
                    }
                } else {
                    notificationsSkipped += tokens.size
                    logger.info("Skipping notification for user $userId: chapter $currentChapterNumber >= ${chapter.chapterNumber}")
                }
            }
            
            logger.info("=== END: Notifications sent=$notificationsSent, skipped=$notificationsSkipped ===")
        } catch (e: Exception) {
            // Log error but don't fail chapter creation
            logger.error("Error sending chapter update notifications: ${e.message}", e)
            logger.error("Stack trace:", e)
        }
    }

    fun updateChapter(novelId: String, chapterId: String, request: ChapterUpdateRequest): ChapterResponseDto {
        val existingChapter = chapterRepository.findById(chapterId)
            .orElseThrow { Exception("Chapter with ID '$chapterId' not found") }
        
        // Verify that the chapter belongs to the specified novel
        if (existingChapter.novelId != novelId) {
            throw Exception("Chapter with ID '$chapterId' does not belong to novel '$novelId'")
        }
        
        val updatedChapter = existingChapter.copy(
            chapterTitle = request.chapterTitle,
            content = request.content,
            wordCount = request.content.split("\\s+".toRegex()).size
        )
        val savedChapter = chapterRepository.save(updatedChapter)
        
        // Update novel stats (wordCount) from all chapters if content changed
        if (existingChapter.wordCount != savedChapter.wordCount) {
            try {
                novelService.updateNovelStatsFromChapters(novelId)
            } catch (e: Exception) {
                val logger = org.slf4j.LoggerFactory.getLogger(ChapterService::class.java)
                logger.error("Failed to update novel stats after chapter update: ${e.message}", e)
                // Don't fail chapter update if stats update fails
            }
        }
        
        return ChapterResponseDto(
            id = savedChapter.id!!,
            novelId = savedChapter.novelId,
            chapterTitle = savedChapter.chapterTitle,
            chapterNumber = savedChapter.chapterNumber,
            content = savedChapter.content,
            wordCount = savedChapter.wordCount,
            viewCount = savedChapter.viewCount,
            createdAt = savedChapter.createdAt.toString(),
            updatedAt = savedChapter.updatedAt.toString()
        )
    }

    fun deleteChapter(@PathVariable("chapterId") chapterId: String): ResponseEntity<Nothing> {
        val chapter = chapterRepository.findById(chapterId)
            .orElseThrow { Exception("Chapter with ID '$chapterId' not found") }
        
        val novelId = chapter.novelId
        
        chapterRepository.deleteById(chapterId)
        
        // Update novel stats (chapterCount, wordCount) from remaining chapters
        try {
            novelService.updateNovelStatsFromChapters(novelId)
        } catch (e: Exception) {
            val logger = org.slf4j.LoggerFactory.getLogger(ChapterService::class.java)
            logger.error("Failed to update novel stats after chapter deletion: ${e.message}", e)
            // Don't fail chapter deletion if stats update fails
        }
        
        return ResponseEntity.noContent().build()
    }
    fun getChapterById(chapterId: String): ChapterResponseDto {
        val chapter = chapterRepository.findById(chapterId)
            .orElseThrow { Exception("Chapter with ID '$chapterId' not found") }
        return ChapterResponseDto(
            id = chapter.id!!,
            novelId = chapter.novelId,
            chapterTitle = chapter.chapterTitle,
            chapterNumber = chapter.chapterNumber,
            content = chapter.content,
            wordCount = chapter.wordCount,
            viewCount = chapter.viewCount,
            createdAt = chapter.createdAt.toString(),
            updatedAt = chapter.updatedAt.toString()
        )
    }

    fun getChapterByNovelAndChapterId(novelId: String, chapterId: String): ChapterResponseDto {
        val chapter = chapterRepository.findById(chapterId)
            .orElseThrow { Exception("Chapter with ID '$chapterId' not found") }
        
        // Verify that the chapter belongs to the specified novel
        if (chapter.novelId != novelId) {
            throw Exception("Chapter with ID '$chapterId' does not belong to novel '$novelId'")
        }
        
        return ChapterResponseDto(
            id = chapter.id!!,
            novelId = chapter.novelId,
            chapterTitle = chapter.chapterTitle,
            chapterNumber = chapter.chapterNumber,
            content = chapter.content,
            wordCount = chapter.wordCount,
            viewCount = chapter.viewCount,
            createdAt = chapter.createdAt.toString(),
            updatedAt = chapter.updatedAt.toString()
        )
    }

    @Transactional(readOnly = true)
    fun getChaptersByNovelId(novelId: String, page: Int = 0, size: Int = 20, sortDirection: String = "asc"): PageResponse<ChapterResponseDto> {
        val sort = Sort.by(
            if (sortDirection == "asc") Sort.Direction.ASC else Sort.Direction.DESC,
            "chapterNumber"
        )
        val pageable: Pageable = PageRequest.of(page, size, sort)
        val chapters = chapterRepository.findByNovelId(novelId, pageable)
        
        val chapterDtos = chapters.content.map { chapter ->
            ChapterResponseDto(
                id = chapter.id!!,
                novelId = chapter.novelId,
                chapterTitle = chapter.chapterTitle,
                chapterNumber = chapter.chapterNumber,
                content = chapter.content,
                wordCount = chapter.wordCount,
                viewCount = chapter.viewCount,
                createdAt = chapter.createdAt.toString(),
                updatedAt = chapter.updatedAt.toString()
            )
        }
        
        return PageResponse(
            content = chapterDtos,
            page = chapters.number,
            size = chapters.size,
            totalElements = chapters.totalElements,
            totalPages = chapters.totalPages,
            first = chapters.isFirst,
            last = chapters.isLast,
            numberOfElements = chapters.numberOfElements
        )
    }

    fun reorderChapters(novelId: String, newOrder: List<String>): List<ChapterResponseDto> {
        val chapters = chapterRepository.findByNovelId(novelId)
        if (chapters.size != newOrder.size || !chapters.all { newOrder.contains(it.id) }) {
            throw Exception("Invalid chapter order")
        }
        val chapterMap = chapters.associateBy { it.id }
        val reorderedChapters = newOrder.mapIndexed { index, chapterId ->
            val chapter = chapterMap[chapterId] ?: throw Exception("Chapter with ID '$chapterId' not found")
            chapter.copy(chapterNumber = index + 1)
        }
        chapterRepository.saveAll(reorderedChapters)
        return reorderedChapters.map { chapter ->
            ChapterResponseDto(
                id = chapter.id!!,
                novelId = chapter.novelId,
                chapterTitle = chapter.chapterTitle,
                chapterNumber = chapter.chapterNumber,
                content = chapter.content,
                wordCount = chapter.wordCount,
                viewCount = chapter.viewCount,
                createdAt = chapter.createdAt.toString(),
                updatedAt = chapter.updatedAt.toString()
            )
        }
    }

    fun incrementViewCount(chapterId: String): ChapterResponseDto {
        val chapter = chapterRepository.findById(chapterId)
            .orElseThrow { Exception("Chapter with ID '$chapterId' not found") }

        val updatedChapter = chapter.copy(
            viewCount = chapter.viewCount + 1,
            updatedAt = java.time.LocalDateTime.now()
        )

        val savedChapter = chapterRepository.save(updatedChapter)
        
        // Also increment the novel's view count
        novelService.incrementViewCount(chapter.novelId)
        
        return ChapterResponseDto(
            id = savedChapter.id!!,
            novelId = savedChapter.novelId,
            chapterTitle = savedChapter.chapterTitle,
            chapterNumber = savedChapter.chapterNumber,
            content = savedChapter.content,
            wordCount = savedChapter.wordCount,
            viewCount = savedChapter.viewCount,
            createdAt = savedChapter.createdAt.toString(),
            updatedAt = savedChapter.updatedAt.toString()
        )
    }
    fun generateChapterPdf(chapterId: String): ByteArray {
        val chapter = chapterRepository.findById(chapterId)
            .orElseThrow { Exception("Chapter with ID '$chapterId' not found") }

        val document = org.apache.pdfbox.pdmodel.PDDocument()
        try {
            val pageSize = org.apache.pdfbox.pdmodel.common.PDRectangle.A4
            val margin = 50f
            val titleFontSize = 16f
            val bodyFontSize = 12f
            val titleLeading = 22f
            val bodyLeading = 18f

            // Load Unicode-capable font if available; fallback to built-in fonts
            val resourceStream = this::class.java.getResourceAsStream("/fonts/NotoSerif-Regular.ttf")
            val titleFont: org.apache.pdfbox.pdmodel.font.PDFont
            val bodyFont: org.apache.pdfbox.pdmodel.font.PDFont
            if (resourceStream != null) {
                val unicodeFont = org.apache.pdfbox.pdmodel.font.PDType0Font.load(document, resourceStream, true)
                titleFont = unicodeFont
                bodyFont = unicodeFont
            } else {
                // Use standard fonts that support basic Unicode characters
                titleFont = org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD
                bodyFont = org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA
            }

            // Sanitize text to remove unsupported Unicode characters
            val sanitizedTitle = chapter.chapterTitle.replace(Regex("[^\\x00-\\x7F]"), "?")
            val sanitizedContent = chapter.content.replace(Regex("[^\\x00-\\x7F]"), "?")

            fun wrapText(text: String, font: org.apache.pdfbox.pdmodel.font.PDFont, fontSize: Float, maxWidth: Float): List<String> {
                val result = mutableListOf<String>()
                val paragraphs = text.replace("\r\n", "\n").replace("\r", "\n").split("\n")
                for (para in paragraphs) {
                    val words = para.split(" ")
                    var line = StringBuilder()
                    for (word in words) {
                        val tentative = if (line.isEmpty()) word else "${line} $word"
                        val width = font.getStringWidth(tentative) / 1000 * fontSize
                        if (width <= maxWidth) {
                            line = StringBuilder(tentative)
                        } else {
                            if (line.isNotEmpty()) {
                                result.add(line.toString())
                            }
                            // If single word is too long, hard-break it
                            var remaining = word
                            while (font.getStringWidth(remaining) / 1000 * fontSize > maxWidth && remaining.length > 1) {
                                var i = remaining.length - 1
                                var broken = false
                                while (i > 0) {
                                    val part = remaining.substring(0, i)
                                    val w = font.getStringWidth(part) / 1000 * fontSize
                                    if (w <= maxWidth) {
                                        result.add(part)
                                        remaining = remaining.substring(i)
                                        broken = true
                                        break
                                    }
                                    i--
                                }
                                if (!broken) break
                            }
                            line = StringBuilder(remaining)
                        }
                    }
                    if (line.isNotEmpty()) {
                        result.add(line.toString())
                    }
                    // Blank line between paragraphs
                    result.add("")
                }
                // Remove trailing blank inserted after last paragraph
                if (result.isNotEmpty() && result.last().isEmpty()) {
                    result.removeAt(result.size - 1)
                }
                return result
            }

            var page = org.apache.pdfbox.pdmodel.PDPage(pageSize)
            document.addPage(page)

            var yPosition = pageSize.height - margin

            // Draw title
            run {
                val contentStream = org.apache.pdfbox.pdmodel.PDPageContentStream(
                    document,
                    page,
                    org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode.APPEND,
                    true
                )
                contentStream.beginText()
                contentStream.setFont(titleFont, titleFontSize)
                contentStream.newLineAtOffset(margin, yPosition)
                contentStream.showText(sanitizedTitle)
                contentStream.endText()
                contentStream.close()
                yPosition -= titleLeading
            }

            // Draw content with wrapping and pagination
            val lines = wrapText(sanitizedContent, bodyFont, bodyFontSize, pageSize.width - 2 * margin)

            var contentStream = org.apache.pdfbox.pdmodel.PDPageContentStream(
                document,
                page,
                org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode.APPEND,
                true
            )
            contentStream.beginText()
            contentStream.setFont(bodyFont, bodyFontSize)
            contentStream.setLeading(bodyLeading)
            contentStream.newLineAtOffset(margin, yPosition)

            val bottomY = margin
            for (line in lines) {
                if (yPosition - bodyLeading < bottomY) {
                    contentStream.endText()
                    contentStream.close()
                    // new page
                    page = org.apache.pdfbox.pdmodel.PDPage(pageSize)
                    document.addPage(page)
                    yPosition = pageSize.height - margin
                    contentStream = org.apache.pdfbox.pdmodel.PDPageContentStream(
                        document,
                        page,
                        org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode.APPEND,
                        true
                    )
                    contentStream.beginText()
                    contentStream.setFont(bodyFont, bodyFontSize)
                    contentStream.setLeading(bodyLeading)
                    contentStream.newLineAtOffset(margin, yPosition)
                }
                contentStream.showText(line)
                contentStream.newLineAtOffset(0f, -bodyLeading)
                yPosition -= bodyLeading
            }

            contentStream.endText()
            contentStream.close()

            val baos = java.io.ByteArrayOutputStream()
            document.save(baos)
            return baos.toByteArray()
        } finally {
            document.close()
        }
    }
}
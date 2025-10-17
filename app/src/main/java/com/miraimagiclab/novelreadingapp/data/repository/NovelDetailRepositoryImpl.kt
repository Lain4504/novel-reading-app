package com.miraimagiclab.novelreadingapp.data.repository

import com.miraimagiclab.novelreadingapp.data.BookDetail
import com.miraimagiclab.novelreadingapp.data.mapper.NovelDetailMapper
import com.miraimagiclab.novelreadingapp.data.remote.api.ChapterApiService
import com.miraimagiclab.novelreadingapp.data.remote.api.CommentApiService
import com.miraimagiclab.novelreadingapp.data.remote.api.NovelApiService
import com.miraimagiclab.novelreadingapp.data.remote.api.ReviewApiService
import com.miraimagiclab.novelreadingapp.domain.repository.NovelDetailRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NovelDetailRepositoryImpl @Inject constructor(
    private val novelApiService: NovelApiService,
    private val chapterApiService: ChapterApiService,
    private val commentApiService: CommentApiService,
    private val reviewApiService: ReviewApiService
) : NovelDetailRepository {

    private val cachedBookDetails = mutableMapOf<String, BookDetail>()

    override fun getNovelDetail(id: String): Flow<BookDetail?> = flow {
        emit(cachedBookDetails[id])
    }

    override suspend fun refreshNovelDetail(id: String) {
        try {
            coroutineScope {
                // Fetch all data in parallel
                val novelDeferred = async { novelApiService.getNovelById(id) }
                val chaptersDeferred = async { chapterApiService.getChaptersByNovelId(id) }
                val commentsDeferred = async { commentApiService.getCommentsByNovel(id) }
                val reviewsDeferred = async { reviewApiService.getReviewsByNovel(id) }
                val recommendationsDeferred = async { novelApiService.getTopNovelsByRating() }

                // Wait for all requests to complete
                val novelResponse = novelDeferred.await()
                val chaptersResponse = chaptersDeferred.await()
                val commentsResponse = commentsDeferred.await()
                val reviewsResponse = reviewsDeferred.await()
                val recommendationsResponse = recommendationsDeferred.await()

                // Check if all responses are successful
                if (novelResponse.success && novelResponse.data != null &&
                    chaptersResponse.success && chaptersResponse.data != null &&
                    commentsResponse.success && commentsResponse.data != null &&
                    reviewsResponse.success && reviewsResponse.data != null &&
                    recommendationsResponse.success && recommendationsResponse.data != null) {

                    val novelDto = novelResponse.data!!
                    val chapters = chaptersResponse.data!!.content
                    val comments = commentsResponse.data!!.content
                    val reviews = reviewsResponse.data!!.content
                    val recommendations = recommendationsResponse.data!!.filter { it.id != id }.take(3) // Exclude current novel

                    // Create BookDetail using mapper
                    val bookDetail = NovelDetailMapper.createBookDetail(
                        novelDto = novelDto,
                        chapters = chapters,
                        comments = comments,
                        reviews = reviews,
                        recommendations = recommendations
                    )

                    // Cache the result
                    cachedBookDetails[id] = bookDetail
                }
            }
        } catch (e: Exception) {
            // Handle error - cached data will still be available if it exists
            throw e
        }
    }
}



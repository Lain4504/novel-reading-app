package com.miraimagiclab.novelreadingapp.data.repository

import com.miraimagiclab.novelreadingapp.data.mapper.NovelDetailMapper
import com.miraimagiclab.novelreadingapp.data.mapper.UserNovelInteractionMapper
import com.miraimagiclab.novelreadingapp.data.remote.api.ChapterApiService
import com.miraimagiclab.novelreadingapp.data.remote.api.CommentApiService
import com.miraimagiclab.novelreadingapp.data.remote.api.NovelApiService
import com.miraimagiclab.novelreadingapp.data.remote.api.ReviewApiService
import com.miraimagiclab.novelreadingapp.domain.model.NovelDetail
import com.miraimagiclab.novelreadingapp.domain.model.UserNovelInteraction
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
    private val reviewApiService: ReviewApiService,
    private val userNovelInteractionApiService: com.miraimagiclab.novelreadingapp.data.remote.api.UserNovelInteractionApiService
) : NovelDetailRepository {

    private val cachedNovelDetails = mutableMapOf<String, NovelDetail>()

    override fun getNovelDetail(id: String): Flow<NovelDetail?> = flow {
        emit(cachedNovelDetails[id])
    }

    override suspend fun refreshNovelDetail(id: String) {
        try {
            coroutineScope {
                // Fetch all data in parallel
                val novelDeferred = async { novelApiService.getNovelById(id) }
                val chaptersDeferred = async { chapterApiService.getChaptersByNovelId(id) }
                val commentsDeferred = async { commentApiService.getCommentsByNovel(id) }
                val reviewsDeferred = async { reviewApiService.getReviewsByNovel(id) }
                val recommendationsDeferred = async { 
                    try {
                        novelApiService.getRecommendationsByNovel(id, 5)
                    } catch (e: Exception) {
                        // Fallback to top rated novels if recommendations endpoint is not available
                        novelApiService.getTopNovelsByRating()
                    }
                }

                // Wait for all requests to complete
                val novelResponse = novelDeferred.await()
                val chaptersResponse = chaptersDeferred.await()
                val commentsResponse = commentsDeferred.await()
                val reviewsResponse = reviewsDeferred.await()
                val recommendationsResponse = recommendationsDeferred.await()

                // Check if novel response is successful (required)
                if (!novelResponse.success || novelResponse.data == null) {
                    throw Exception("Failed to load novel details: ${novelResponse.message ?: "Unknown error"}")
                }

                val novelDto = novelResponse.data!!
                
                // Handle other responses with fallbacks for partial failures
                val chapters = if (chaptersResponse.success && chaptersResponse.data != null) {
                    chaptersResponse.data!!.content
                } else {
                    emptyList() // Fallback to empty list
                }
                
                val comments = if (commentsResponse.success && commentsResponse.data != null) {
                    commentsResponse.data!!.content
                } else {
                    emptyList() // Fallback to empty list
                }
                
                val reviews = if (reviewsResponse.success && reviewsResponse.data != null) {
                    reviewsResponse.data!!.content
                } else {
                    emptyList() // Fallback to empty list
                }
                
                val recommendations = if (recommendationsResponse.success && recommendationsResponse.data != null) {
                    recommendationsResponse.data!!.filter { it.id != id }.take(3) // Exclude current novel
                } else {
                    emptyList() // Fallback to empty list
                }

                // Create NovelDetail using mapper
                val novelDetail = NovelDetailMapper.createNovelDetail(
                    novelDto = novelDto,
                    chapters = chapters,
                    comments = comments,
                    reviews = reviews,
                    recommendations = recommendations
                )

                // Cache the result
                cachedNovelDetails[id] = novelDetail
            }
        } catch (e: Exception) {
            // Handle error - cached data will still be available if it exists
            throw e
        }
    }

    override fun getUserInteraction(userId: String, novelId: String): Flow<UserNovelInteraction?> = flow {
        try {
            val response = userNovelInteractionApiService.getUserNovelInteraction(userId, novelId)
            if (response.success && response.data != null) {
                // Convert DTO to domain model using mapper
                val interaction = response.data!!
                val domainInteraction = UserNovelInteractionMapper.mapDtoToDomain(interaction)
                emit(domainInteraction)
            } else {
                emit(null)
            }
        } catch (e: Exception) {
            emit(null)
        }
    }

    override suspend fun toggleFollow(userId: String, novelId: String): UserNovelInteraction {
        val response = userNovelInteractionApiService.toggleFollow(userId, novelId)
        if (response.success && response.data != null) {
            val interaction = response.data!!
            return UserNovelInteractionMapper.mapDtoToDomain(interaction)
        } else {
            throw Exception("Failed to toggle follow: ${response.message}")
        }
    }

    override suspend fun toggleWishlist(userId: String, novelId: String): UserNovelInteraction {
        val response = userNovelInteractionApiService.toggleWishlist(userId, novelId)
        if (response.success && response.data != null) {
            val interaction = response.data!!
            return UserNovelInteractionMapper.mapDtoToDomain(interaction)
        } else {
            throw Exception("Failed to toggle wishlist: ${response.message}")
        }
    }
}



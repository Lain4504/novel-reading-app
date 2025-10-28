package com.miraimagiclab.novelreadingapp.data.repository

import com.miraimagiclab.novelreadingapp.data.remote.api.ChapterApiService
import com.miraimagiclab.novelreadingapp.data.remote.api.NovelApiService
import com.miraimagiclab.novelreadingapp.data.remote.dto.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthorRepository @Inject constructor(
    private val novelApiService: NovelApiService,
    private val chapterApiService: ChapterApiService
) {
    // Novel operations
    suspend fun createNovel(
        title: String,
        description: String,
        authorName: String,
        authorId: String?,
        categories: Set<String>,
        status: String = "DRAFT",
        isR18: Boolean = false,
        coverImageUrl: String? = null
    ): Result<NovelDto> {
        return try {
            val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val authorNameBody = authorName.toRequestBody("text/plain".toMediaTypeOrNull())
            val authorIdBody = authorId?.toRequestBody("text/plain".toMediaTypeOrNull())
            val categoriesBody = categories.joinToString(",").toRequestBody("text/plain".toMediaTypeOrNull())
            val statusBody = status.toRequestBody("text/plain".toMediaTypeOrNull())
            val isR18Body = isR18.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val coverUrlBody = coverImageUrl?.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = novelApiService.createNovel(
                title = titleBody,
                description = descriptionBody,
                authorName = authorNameBody,
                authorId = authorIdBody,
                categories = categoriesBody,
                status = statusBody,
                isR18 = isR18Body,
                coverImage = null,  // Don't send file
                coverUrl = coverUrlBody  // Send URL instead
            )

            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.message() ?: "Failed to create novel"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateNovel(
        novelId: String,
        title: String? = null,
        description: String? = null,
        authorName: String? = null,
        authorId: String? = null,
        categories: Set<String>? = null,
        rating: Double? = null,
        wordCount: Int? = null,
        chapterCount: Int? = null,
        status: String? = null,
        isR18: Boolean? = null,
        coverImageUrl: String? = null
    ): Result<NovelDto> {
        return try {
            val titleBody = title?.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionBody = description?.toRequestBody("text/plain".toMediaTypeOrNull())
            val authorNameBody = authorName?.toRequestBody("text/plain".toMediaTypeOrNull())
            val authorIdBody = authorId?.toRequestBody("text/plain".toMediaTypeOrNull())
            val categoriesBody = categories?.joinToString(",")?.toRequestBody("text/plain".toMediaTypeOrNull())
            val ratingBody = rating?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val wordCountBody = wordCount?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val chapterCountBody = chapterCount?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val statusBody = status?.toRequestBody("text/plain".toMediaTypeOrNull())
            val isR18Body = isR18?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val coverUrlBody = coverImageUrl?.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = novelApiService.updateNovel(
                id = novelId,
                title = titleBody,
                description = descriptionBody,
                authorName = authorNameBody,
                authorId = authorIdBody,
                categories = categoriesBody,
                rating = ratingBody,
                wordCount = wordCountBody,
                chapterCount = chapterCountBody,
                status = statusBody,
                isR18 = isR18Body,
                coverImage = null,  // Don't send file
                coverUrl = coverUrlBody  // Send URL instead
            )

            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.message() ?: "Failed to update novel"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteNovel(novelId: String): Result<Unit> {
        return try {
            val response = novelApiService.deleteNovel(novelId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message() ?: "Failed to delete novel"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNovelsByAuthor(authorId: String, page: Int = 0, size: Int = 20): Result<PageResponse<NovelDto>> {
        return try {
            val response = novelApiService.getNovelsByAuthor(authorId, page, size)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.message() ?: "Failed to get author novels"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Chapter operations
    suspend fun createChapter(
        novelId: String,
        chapterTitle: String,
        chapterNumber: Int,
        content: String
    ): Result<ChapterDto> {
        return try {
            val request = ChapterCreateRequest(
                chapterTitle = chapterTitle,
                chapterNumber = chapterNumber,
                content = content
            )
            val response = chapterApiService.createChapter(novelId, request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.message() ?: "Failed to create chapter"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateChapter(
        novelId: String,
        chapterId: String,
        chapterTitle: String? = null,
        chapterNumber: Int? = null,
        content: String? = null
    ): Result<ChapterDto> {
        return try {
            val request = ChapterUpdateRequest(
                chapterTitle = chapterTitle,
                chapterNumber = chapterNumber,
                content = content
            )
            val response = chapterApiService.updateChapter(novelId, chapterId, request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.message() ?: "Failed to update chapter"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteChapter(chapterId: String): Result<Unit> {
        return try {
            val response = chapterApiService.deleteChapter(chapterId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message() ?: "Failed to delete chapter"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getChaptersByNovel(novelId: String, page: Int = 0, size: Int = 20): Result<PageResponse<ChapterDto>> {
        return try {
            val response = chapterApiService.getChaptersByNovelId(novelId, page, size)
            if (response.success) {
                Result.success(response.data!!)
            } else {
                Result.failure(Exception("Failed to get chapters"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNovelById(novelId: String): Result<NovelDto> {
        return try {
            val response = novelApiService.getNovelById(novelId)
            if (response.success && response.data != null) {
                Result.success(response.data!!)
            } else {
                Result.failure(Exception(response.message ?: "Failed to get novel"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getChapterById(novelId: String, chapterId: String): Result<ChapterDto> {
        return try {
            val response = chapterApiService.getChapterById(novelId, chapterId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.message() ?: "Failed to get chapter"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

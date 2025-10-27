package com.miraimagiclab.novelreadingapp.data.repository

import com.miraimagiclab.novelreadingapp.data.remote.api.ImageApiService
import com.miraimagiclab.novelreadingapp.data.remote.dto.ImageResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor(
    private val imageApiService: ImageApiService
) {
    suspend fun uploadImage(
        imageFile: File,
        ownerId: String,
        ownerType: String = "USER"
    ): Result<ImageResponse> {
        return try {
            val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", imageFile.name, requestBody)
            val ownerIdBody = ownerId.toRequestBody("text/plain".toMediaTypeOrNull())
            val ownerTypeBody = ownerType.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = imageApiService.uploadImage(filePart, ownerIdBody, ownerTypeBody)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message() ?: "Failed to upload image"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


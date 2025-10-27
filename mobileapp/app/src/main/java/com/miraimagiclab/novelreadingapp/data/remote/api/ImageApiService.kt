package com.miraimagiclab.novelreadingapp.data.remote.api

import com.miraimagiclab.novelreadingapp.data.remote.dto.ImageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImageApiService {

    @Multipart
    @POST("images/upload")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("ownerId") ownerId: RequestBody,
        @Part("ownerType") ownerType: RequestBody
    ): Response<ImageResponse>
}


package com.miraimagiclab.novelreadingapp.config

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CloudinaryConfig {

    @Bean
    fun cloudinary(
        @Value("\${cloudinary.cloud-name}") cloudName: String,
        @Value("\${cloudinary.api-key}") apiKey: String,
        @Value("\${cloudinary.api-secret}") apiSecret: String
    ): Cloudinary {
        return Cloudinary(
            ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true,
                "chunk_size", 6000000, // 6MB chunk size for large file uploads
                "timeout", 60000, // 60 seconds timeout
                "upload_preset", "ml_default" // Optional: set a default upload preset
            )
        )
    }
}

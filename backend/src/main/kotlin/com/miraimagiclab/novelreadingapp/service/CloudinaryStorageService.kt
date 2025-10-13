package com.miraimagiclab.novelreadingapp.service

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class CloudinaryStorageService(
    private val cloudinary: Cloudinary
) {

    fun upload(file: MultipartFile, folder: String = "novel_app"): String {
        val uploadResult = cloudinary.uploader().upload(
            file.bytes,
            ObjectUtils.asMap("folder", folder)
        )
        return uploadResult["public_id"]?.toString()
            ?: throw RuntimeException("Upload to Cloudinary failed!")
    }

    fun delete(publicId: String) {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap())
    }
}

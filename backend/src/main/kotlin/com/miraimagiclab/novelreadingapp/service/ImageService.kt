package com.miraimagiclab.novelreadingapp.service

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.miraimagiclab.novelreadingapp.dto.request.ImageUploadRequest
import com.miraimagiclab.novelreadingapp.dto.response.ImageResponse
import com.miraimagiclab.novelreadingapp.exception.ImageNotFoundException
import com.miraimagiclab.novelreadingapp.model.Image
import com.miraimagiclab.novelreadingapp.repository.ImageRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@Service
class ImageService(
    private val cloudinary: Cloudinary,
    private val imageRepository: ImageRepository
) {

    fun uploadImage(request: ImageUploadRequest): ImageResponse {
        val file = request.file

        val uploadResult: Map<*, *> = try {
            cloudinary.uploader().upload(file.bytes, ObjectUtils.asMap("folder", "novel_app"))
        } catch (ex: Exception) {
            throw RuntimeException("Failed to upload to Cloudinary: ${ex.message}", ex)
        }

        val secureUrl = uploadResult["secure_url"]?.toString()
            ?: throw RuntimeException("Cloudinary did not return secure_url")
        val publicId = uploadResult["public_id"]?.toString()
            ?: throw RuntimeException("Cloudinary did not return public_id")

        val image = imageRepository.save(
            Image(
                id = null,
                originalFilename = file.originalFilename ?: "unknown",
                contentType = file.contentType ?: "application/octet-stream",
                fileSize = file.size,
                storageKey = publicId,
                ownerId = request.ownerId,
                ownerType = request.ownerType,
                active = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )

        return ImageResponse(
            id = image.id!!,
            url = secureUrl,
            ownerId = image.ownerId,
            ownerType = image.ownerType,
            originalFilename = image.originalFilename,
            contentType = image.contentType,
            fileSize = image.fileSize,
            active = image.active
        )
    }

    fun getImage(id: String): ImageResponse {
        val image = imageRepository.findById(id).orElseThrow { ImageNotFoundException(id) }

        val url = cloudinary.url().secure(true).generate(image.storageKey)

        return ImageResponse(
            id = image.id!!,
            url = url,
            ownerId = image.ownerId,
            ownerType = image.ownerType,
            originalFilename = image.originalFilename,
            contentType = image.contentType,
            fileSize = image.fileSize,
            active = image.active
        )
    }

    /**
     * Redirect / return URL for file access.
     * If you want to stream binary, you can implement fetching by HTTP.
     */
    fun getFileUrl(id: String): String {
        val image = imageRepository.findById(id).orElseThrow { ImageNotFoundException(id) }
        return cloudinary.url().secure(true).generate(image.storageKey)
    }

    /**
     * Update file: replace file on Cloudinary (delete old public_id, upload new one),
     * update metadata in DB.
     */
    fun updateImage(id: String, newFile: MultipartFile): ImageResponse {
        val image = imageRepository.findById(id).orElseThrow { ImageNotFoundException(id) }

        // delete old
        try {
            cloudinary.uploader().destroy(image.storageKey, ObjectUtils.emptyMap())
        } catch (ex: Exception) {
            // log but continue - if destroy fails, we still can upload new
        }

        // upload new
        val uploadResult = try {
            cloudinary.uploader().upload(newFile.bytes, ObjectUtils.asMap("folder", "novel_app"))
        } catch (ex: Exception) {
            throw RuntimeException("Failed to upload new file to Cloudinary: ${ex.message}", ex)
        }

        val newPublicId = uploadResult["public_id"]?.toString()
            ?: throw RuntimeException("Cloudinary did not return public_id for new file")
        val newSecureUrl = uploadResult["secure_url"]?.toString()
            ?: throw RuntimeException("Cloudinary did not return secure_url for new file")

        val updated = image.copy(
            originalFilename = newFile.originalFilename ?: image.originalFilename,
            contentType = newFile.contentType ?: image.contentType,
            fileSize = newFile.size,
            storageKey = newPublicId,
            updatedAt = LocalDateTime.now()
        )

        imageRepository.save(updated)

        return ImageResponse(
            id = updated.id!!,
            url = newSecureUrl,
            ownerId = updated.ownerId,
            ownerType = updated.ownerType,
            originalFilename = updated.originalFilename,
            contentType = updated.contentType,
            fileSize = updated.fileSize,
            active = updated.active
        )
    }

    /**
     * Hard delete: remove from Cloudinary and DB
     */
    fun deleteImage(id: String) {
        val image = imageRepository.findById(id).orElseThrow { ImageNotFoundException(id) }

        try {
            cloudinary.uploader().destroy(image.storageKey, ObjectUtils.emptyMap())
        } catch (ex: Exception) {
            // log error but still attempt to remove DB record (optional)
        }

        imageRepository.delete(image)
    }

    fun listByOwner(ownerId: String, ownerType: String): List<ImageResponse> {
        return imageRepository.findByOwnerIdAndOwnerType(ownerId, ownerType).map { img ->
            val url = try {
                cloudinary.url().secure(true).generate(img.storageKey)
            } catch (_: Exception) {
                ""
            }
            ImageResponse(
                id = img.id!!,
                url = url,
                ownerId = img.ownerId,
                ownerType = img.ownerType,
                originalFilename = img.originalFilename,
                contentType = img.contentType,
                fileSize = img.fileSize,
                active = img.active
            )
        }
    }
}

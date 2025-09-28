package com.miraimagiclab.novelreadingapp.service

import com.miraimagiclab.novelreadingapp.model.Image
import com.miraimagiclab.novelreadingapp.repository.ImageRepository
import com.miraimagiclab.novelreadingapp.exception.NotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ImageService(
    private val imageRepository: ImageRepository
) {
    fun upload(image: Image): Image = imageRepository.save(image)

    fun getById(id: String): Image =
        imageRepository.findById(id).orElseThrow { NotFoundException("Image not found with id: $id") }

    fun getByOwnerId(ownerId: String, pageable: Pageable): Page<Image> =
        imageRepository.findByOwnerId(ownerId, pageable)

    fun delete(id: String) {
        val image = getById(id)
        val inactive = image.copy(
            active = false,
            updatedAt = LocalDateTime.now()
        )
        imageRepository.save(inactive)
    }
}

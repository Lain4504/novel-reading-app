package com.miraimagiclab.novelreadingapp.controller

import com.miraimagiclab.novelreadingapp.dto.ApiResponse
import com.miraimagiclab.novelreadingapp.model.Image
import com.miraimagiclab.novelreadingapp.service.ImageService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/images")
@CrossOrigin(
    origins = [
        "http://localhost:3000",
        "http://localhost:8080",
        "http://127.0.0.1:3000",
        "http://127.0.0.1:8080"
    ]
)
class ImageController(
    private val imageService: ImageService
) {
    @PostMapping
    fun upload(@RequestBody image: Image): ResponseEntity<ApiResponse<Image>> {
        val saved = imageService.upload(image)
        return ResponseEntity.ok(ApiResponse.success(saved, "Image uploaded successfully"))
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): ResponseEntity<ApiResponse<Image>> {
        val image = imageService.getById(id)
        return ResponseEntity.ok(ApiResponse.success(image))
    }

    @GetMapping("/owner/{ownerId}")
    fun getByOwnerId(
        @PathVariable ownerId: String,
        pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<Image>>> {
        val images = imageService.getByOwnerId(ownerId, pageable)
        return ResponseEntity.ok(ApiResponse.success(images))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<ApiResponse<Unit>> {
        imageService.delete(id)
        return ResponseEntity.ok(ApiResponse.success(message = "Image deleted successfully"))
    }
}

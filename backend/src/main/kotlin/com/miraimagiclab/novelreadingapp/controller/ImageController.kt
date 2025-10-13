package com.miraimagiclab.novelreadingapp.controller

import com.miraimagiclab.novelreadingapp.dto.request.ImageUploadRequest
import com.miraimagiclab.novelreadingapp.dto.response.ImageResponse
import com.miraimagiclab.novelreadingapp.service.ImageService
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/images")
class ImageController(
    private val imageService: ImageService
) {

    @PostMapping("/upload")
    fun uploadImage(
        @RequestPart("file") file: MultipartFile,
        @RequestParam ownerId: String,
        @RequestParam ownerType: String
    ): ResponseEntity<ImageResponse> {
        val req = ImageUploadRequest(file = file, ownerId = ownerId, ownerType = ownerType)
        val res = imageService.uploadImage(req)
        return ResponseEntity.ok(res)
    }

    @GetMapping("/{id}")
    fun getImage(@PathVariable id: String): ResponseEntity<ImageResponse> {
        val res = imageService.getImage(id)
        return ResponseEntity.ok(res)
    }

    /**
     * Redirect to Cloudinary secure URL.
     * Mobile (OkHttp/Retrofit) will follow redirect by default.
     */
    @GetMapping("/{id}/file")
    fun getFileRedirect(@PathVariable id: String): ResponseEntity<Void> {
        val url = imageService.getFileUrl(id)
        val headers = HttpHeaders()
        headers.location = java.net.URI.create(url)
        return ResponseEntity.status(302).headers(headers).build()
    }

    @PutMapping("/{id}")
    fun updateImage(
        @PathVariable id: String,
        @RequestPart("file") file: MultipartFile
    ): ResponseEntity<ImageResponse> {
        val res = imageService.updateImage(id, file)
        return ResponseEntity.ok(res)
    }

    @DeleteMapping("/{id}")
    fun deleteImage(@PathVariable id: String): ResponseEntity<Void> {
        imageService.deleteImage(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/owner/{ownerId}")
    fun listByOwner(@PathVariable ownerId: String, @RequestParam ownerType: String): ResponseEntity<List<ImageResponse>> {
        val list = imageService.listByOwner(ownerId, ownerType)
        return ResponseEntity.ok(list)
    }
}

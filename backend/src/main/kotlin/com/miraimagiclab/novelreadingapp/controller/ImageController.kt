package com.miraimagiclab.novelreadingapp.controller

import com.miraimagiclab.novelreadingapp.dto.request.ImageUploadRequest
import com.miraimagiclab.novelreadingapp.dto.response.ImageResponse
import com.miraimagiclab.novelreadingapp.service.ImageService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/images")
class ImageController(
    private val imageService: ImageService
) {

    @Operation(summary = "Upload image", description = "Upload an image by selecting a file from local device")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Image uploaded successfully")
        ]
    )
    @PostMapping("/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadImage(
        @Parameter(description = "Image file to upload", required = true)
        @RequestPart("file") file: MultipartFile,

        @Parameter(description = "Owner ID", example = "123")
        @RequestParam ownerId: String,

        @Parameter(description = "Owner type", example = "USER")
        @RequestParam ownerType: String
    ): ResponseEntity<ImageResponse> {
        val req = ImageUploadRequest(file = file, ownerId = ownerId, ownerType = ownerType)
        val res = imageService.uploadImage(req)
        return ResponseEntity.ok(res)
    }

    @Operation(summary = "Get image by ID")
    @GetMapping("/{id}")
    fun getImage(@PathVariable id: String): ResponseEntity<ImageResponse> {
        val res = imageService.getImage(id)
        return ResponseEntity.ok(res)
    }

    @Operation(summary = "Get file URL and redirect to actual Cloudinary link")
    @GetMapping("/{id}/file")
    fun getFileRedirect(@PathVariable id: String): ResponseEntity<Void> {
        val url = imageService.getFileUrl(id)
        val headers = HttpHeaders()
        headers.location = java.net.URI.create(url)
        return ResponseEntity.status(302).headers(headers).build()
    }

    @Operation(summary = "Update image file", description = "Replace existing image with a new one")
    @PutMapping("/{id}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateImage(
        @PathVariable id: String,

        @Parameter(description = "New image file to replace old one")
        @RequestPart("file") file: MultipartFile
    ): ResponseEntity<ImageResponse> {
        val res = imageService.updateImage(id, file)
        return ResponseEntity.ok(res)
    }

    @Operation(summary = "Delete image")
    @DeleteMapping("/{id}")
    fun deleteImage(@PathVariable id: String): ResponseEntity<Void> {
        imageService.deleteImage(id)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "List images by owner")
    @GetMapping("/owner/{ownerId}")
    fun listByOwner(
        @PathVariable ownerId: String,
        @RequestParam ownerType: String
    ): ResponseEntity<List<ImageResponse>> {
        val list = imageService.listByOwner(ownerId, ownerType)
        return ResponseEntity.ok(list)
    }
}

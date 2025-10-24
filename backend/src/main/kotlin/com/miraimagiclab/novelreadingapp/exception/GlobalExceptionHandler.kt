package com.miraimagiclab.novelreadingapp.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MaxUploadSizeExceededException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(FileSizeExceededException::class)
    fun handleFileSizeExceeded(ex: FileSizeExceededException): ResponseEntity<Map<String, String>> {
        val body: Map<String, String> = mapOf(
            "error" to "File size exceeded",
            "message" to (ex.message ?: "File size exceeds the maximum allowed limit of 5MB"),
            "status" to "413"
        )
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(body)
    }

    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleMaxUploadSizeExceeded(ex: MaxUploadSizeExceededException): ResponseEntity<Map<String, String>> {
        val body: Map<String, String> = mapOf(
            "error" to "File size exceeded",
            "message" to "File size exceeds the maximum allowed limit of 5MB",
            "status" to "413"
        )
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(body)
    }

    @ExceptionHandler(DuplicateUserException::class)
    fun handleDuplicateUser(ex: DuplicateUserException): ResponseEntity<Map<String, String>> {
        val body: Map<String, String> = mapOf(
            "error" to "Duplicate user",
            "message" to (ex.message ?: "User already exists"),
            "status" to "409"
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body)
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(ex: UserNotFoundException): ResponseEntity<Map<String, String>> {
        val body: Map<String, String> = mapOf(
            "error" to "User not found",
            "message" to (ex.message ?: "User not found"),
            "status" to "404"
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        val body: Map<String, String> = mapOf(
            "error" to "Invalid argument",
            "message" to (ex.message ?: "Invalid request"),
            "status" to "400"
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }
}
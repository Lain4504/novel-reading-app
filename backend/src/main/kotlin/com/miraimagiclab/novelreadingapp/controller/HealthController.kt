package com.miraimagiclab.novelreadingapp.controller

import com.miraimagiclab.novelreadingapp.dto.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/health")
class HealthController {

    @GetMapping
    fun health(): ResponseEntity<ApiResponse<Map<String, Any>>> {
        val healthData = mapOf(
            "status" to "UP",
            "timestamp" to LocalDateTime.now(),
            "service" to "Novel Reading App API",
            "version" to "1.0.0"
        )
        return ResponseEntity.ok(ApiResponse.success(healthData, "Service is healthy"))
    }
}

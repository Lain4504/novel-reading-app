package com.miraimagiclab.novelreadingapp.controller

import com.miraimagiclab.novelreadingapp.dto.ApiResponse
import com.miraimagiclab.novelreadingapp.dto.response.RecommendedNovelDto
import com.miraimagiclab.novelreadingapp.service.GeminiRecommendationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Min
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/recommendations")
@CrossOrigin(origins = ["http://localhost:3000", "http://localhost:8080", "http://127.0.0.1:3000", "http://127.0.0.1:8080"])
@Tag(name = "AI Recommendations", description = "AI-powered novel recommendations using Gemini")
class RecommendationController(
    private val geminiRecommendationService: GeminiRecommendationService
) {

    @Operation(
        summary = "Recommend novels for a user",
        description = "Generate AI-based novel recommendations using Gemini 2.5 Flash, with robust fallback when AI is unavailable."
    )
    @GetMapping("/user/{userId}")
    fun recommendForUser(
        @PathVariable userId: String,
        @RequestParam(defaultValue = "10") @Min(1) limit: Int
    ): ResponseEntity<ApiResponse<List<RecommendedNovelDto>>> {
        val recommendations = geminiRecommendationService.recommendForUser(userId, limit)
        return ResponseEntity.ok(ApiResponse.success(recommendations, "Recommendations generated successfully"))
    }

    @Operation(
        summary = "Recommend novels by topic",
        description = "Generate AI-based recommendations from a user-provided topic or prompt using Gemini 2.5 Flash."
    )
    @GetMapping("/topic")
    fun recommendByTopic(
        @RequestParam("query") query: String,
        @RequestParam(defaultValue = "10") @Min(1) limit: Int
    ): ResponseEntity<ApiResponse<List<RecommendedNovelDto>>> {
        val recommendations = geminiRecommendationService.recommendByTopic(query, limit)
        return ResponseEntity.ok(ApiResponse.success(recommendations, "Topic recommendations generated successfully"))
    }

    @Operation(
        summary = "Recommendation service health",
        description = "Simple health endpoint to verify recommendation controller is reachable."
    )
    @GetMapping("/health")
    fun health(): ResponseEntity<ApiResponse<String>> {
        return ResponseEntity.ok(ApiResponse.success("OK", "Recommendation service healthy"))
    }
}
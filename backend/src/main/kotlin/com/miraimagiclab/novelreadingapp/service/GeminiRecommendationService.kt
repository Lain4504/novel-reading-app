package com.miraimagiclab.novelreadingapp.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.miraimagiclab.novelreadingapp.dto.response.NovelDto
import com.miraimagiclab.novelreadingapp.dto.response.RecommendedNovelDto
import com.miraimagiclab.novelreadingapp.enumeration.CategoryEnum
import com.miraimagiclab.novelreadingapp.model.Novel
import com.miraimagiclab.novelreadingapp.repository.NovelRepository
import com.miraimagiclab.novelreadingapp.repository.UserNovelInteractionRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.Duration

@Service
class GeminiRecommendationService(
    private val novelRepository: NovelRepository,
    private val userNovelInteractionRepository: UserNovelInteractionRepository
) {

    @Value("\${gemini.api-key:}")
    private lateinit var apiKey: String

    @Value("\${gemini.model:gemini-2.5-flash}")
    private lateinit var model: String

    @Value("\${gemini.endpoint:https://generativelanguage.googleapis.com}")
    private lateinit var endpoint: String

    @Value("\${gemini.timeout-ms:15000}")
    private var timeoutMs: Long = 15000

    private val mapper = ObjectMapper()

    fun recommendForUser(userId: String, limit: Int = 10): List<RecommendedNovelDto> {
        val safeLimit = limit.coerceIn(1, 50)
        return try {
            // If API key missing, fallback immediately
            if (!::apiKey.isInitialized || apiKey.isBlank()) {
                return fallback(userId, safeLimit)
            }

            // Load user interactions and derive preferences
            val interactions = userNovelInteractionRepository.findByUserId(userId)
            val interactedIds = interactions.map { it.novelId }.toSet()

            // Load novels from interactions to infer preferences
            val historyNovels = interactions.mapNotNull { inter ->
                novelRepository.findById(inter.novelId).orElse(null)
            }

            // Derive top categories from user history
            val categoryCounts = mutableMapOf<CategoryEnum, Int>()
            historyNovels.forEach { n ->
                n.categories.forEach { c ->
                    categoryCounts[c] = (categoryCounts[c] ?: 0) + 1
                }
            }
            val topCategories = categoryCounts.entries
                .sortedByDescending { it.value }
                .take(5)
                .map { it.key.name }

            // Build candidate pool (top by views, first 100), excluding already interacted
            val candidates = novelRepository.findAll(
                PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "viewCount"))
            ).content.filter { !interactedIds.contains(it.id) }

            if (candidates.isEmpty()) {
                return fallback(userId, safeLimit)
            }

            // Construct prompt with strict JSON contract
            val userHistoryPayload = historyNovels.map {
                mapOf(
                    "id" to (it.id ?: ""),
                    "title" to it.title,
                    "categories" to it.categories.map { c -> c.name },
                    "rating" to it.rating,
                    "follows" to it.followCount,
                    "views" to it.viewCount,
                    "isR18" to it.isR18
                )
            }

            val candidatesPayload = candidates.map {
                mapOf(
                    "id" to (it.id ?: ""),
                    "title" to it.title,
                    "author" to it.authorName,
                    "categories" to it.categories.map { c -> c.name },
                    "rating" to it.rating,
                    "follows" to it.followCount,
                    "views" to it.viewCount,
                    "isR18" to it.isR18
                )
            }

            val instruction = """
You are an AI recommendation engine for a novel reading app. 
Given user reading history preferences and a candidate pool, return the top recommendations.

Rules:
- Return ONLY valid JSON matching this schema (no extra text or markdown):
{
  "recommendations": [
    { "novelId": "string", "score": 0.0-1.0, "reason": "string" }
  ]
}
- "score" is a confidence score between 0 and 1.
- "reason" must be concise (<= 120 chars) and explain category/author/quality fit.
- Do NOT include novels the user already interacted with.
- Prefer user's top categories and similar authors; consider rating/follows/views as quality signals.
- Avoid R18 content if user's history doesn't include it.

User top categories (derived): ${topCategories.joinToString(", ")}

User history (sampled objects):
${mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userHistoryPayload.take(20))}

Candidates (rank among these):
${mapper.writerWithDefaultPrettyPrinter().writeValueAsString(candidatesPayload)}
""".trimIndent()

            val modelResponseText = callGemini(instruction)
            val recs = parseRecommendations(modelResponseText)

            // Map to domain objects
            val candidateById = candidates.associateBy { it.id }
            val resolved = recs.asSequence()
                .mapNotNull { r ->
                    val novel = candidateById[r.novelId] ?: novelRepository.findById(r.novelId).orElse(null)
                    novel?.let { RecommendedNovelDto(NovelDto.fromEntity(it), r.score.coerceIn(0.0, 1.0), r.reason) }
                }
                .filter { it.novel.id.isNotBlank() }
                .take(safeLimit)
                .toList()

            if (resolved.isEmpty()) fallback(userId, safeLimit) else resolved
        } catch (e: Exception) {
            fallback(userId, safeLimit)
        }
    }

    private fun callGemini(prompt: String): String {
        val client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(timeoutMs))
            .build()

        val url = "${endpoint.trimEnd('/')}/v1beta/models/${model.trim()}:generateContent?key=${apiKey.trim()}"

        val payload = mapOf(
            "contents" to listOf(
                mapOf(
                    "role" to "user",
                    "parts" to listOf(mapOf("text" to prompt))
                )
            ),
            "generationConfig" to mapOf(
                "response_mime_type" to "application/json"
            )
        )

        val body = mapper.writeValueAsString(payload)

        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofMillis(timeoutMs))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() !in 200..299) {
            throw RuntimeException("Gemini request failed with status ${response.statusCode()}")
        }

        // Expected structure:
        // {
        //   "candidates": [
        //     { "content": { "parts": [ { "text": "{...json...}" } ] } }
        //   ]
        // }
        val node: JsonNode = mapper.readTree(response.body())
        val text = node.path("candidates").get(0)?.path("content")?.path("parts")?.get(0)?.path("text")?.asText(null)
        return text ?: response.body()
    }

    private data class RawRec(val novelId: String, val score: Double, val reason: String)

    private fun parseRecommendations(text: String): List<RawRec> {
        // Remove code fences if present
        val cleaned = text
            .replace("```json", "")
            .replace("```JSON", "")
            .replace("```", "")
            .trim()

        // Try object with "recommendations"
        try {
            if (cleaned.startsWith("{")) {
                val obj = mapper.readTree(cleaned)
                val arr = obj.path("recommendations")
                if (arr.isArray) {
                    return arr.mapNotNull { toRawRec(it) }
                }
            }
        } catch (_: Exception) { /* ignore and try next strategy */ }

        // Try parse as array
        try {
            if (cleaned.startsWith("[")) {
                val arr = mapper.readTree(cleaned)
                if (arr.isArray) {
                    return arr.mapNotNull { toRawRec(it) }
                }
            }
        } catch (_: Exception) { /* ignore and try next strategy */ }

        // Try to extract first JSON object with "recommendations"
        val startIdx = cleaned.indexOf('{')
        val endIdx = cleaned.lastIndexOf('}')
        if (startIdx >= 0 && endIdx > startIdx) {
            val slice = cleaned.substring(startIdx, endIdx + 1)
            try {
                val obj = mapper.readTree(slice)
                val arr = obj.path("recommendations")
                if (arr.isArray) {
                    return arr.mapNotNull { toRawRec(it) }
                }
            } catch (_: Exception) { /* last resort fails */ }
        }

        return emptyList()
    }

    private fun toRawRec(node: JsonNode): RawRec? {
        val id = node.path("novelId").asText(null) ?: return null
        val score = node.path("score").asDouble(0.5)
        val reason = node.path("reason").asText("") ?: ""
        if (id.isBlank()) return null
        return RawRec(id, score, reason.take(200))
    }

    fun recommendByTopic(topic: String, limit: Int = 10): List<RecommendedNovelDto> {
        val safeLimit = limit.coerceIn(1, 50)
        return try {
            if (!::apiKey.isInitialized || apiKey.isBlank()) {
                return fallback("", safeLimit)
            }

            val query = topic.trim()
            if (query.isBlank()) {
                return fallback("", safeLimit)
            }

            // Build candidate pool: top by views (first 150)
            val candidates = novelRepository.findAll(
                PageRequest.of(0, 150, Sort.by(Sort.Direction.DESC, "viewCount"))
            ).content

            if (candidates.isEmpty()) {
                return emptyList()
            }

            val candidatesPayload = candidates.map {
                mapOf(
                    "id" to (it.id ?: ""),
                    "title" to it.title,
                    "author" to it.authorName,
                    "categories" to it.categories.map { c -> c.name },
                    "rating" to it.rating,
                    "follows" to it.followCount,
                    "views" to it.viewCount,
                    "isR18" to it.isR18
                )
            }

            val instruction = """
You are an AI recommendation engine for a novel reading app.
User provided topic: "$query".

Select the best matching novels from candidates.

Rules:
- Return ONLY valid JSON matching this schema (no extra text or markdown):
{
  "recommendations": [
    { "novelId": "string", "score": 0.0-1.0, "reason": "string" }
  ]
}
- "reason" must be concise (<= 120 chars) explaining how it matches the topic (genre, theme, mood, author).
- Prefer category and description fit; also consider rating/follows/views as quality signals.
- Avoid R18 content unless the topic explicitly requests it.

Candidates (rank among these):
${mapper.writerWithDefaultPrettyPrinter().writeValueAsString(candidatesPayload)}
""".trimIndent()

            val modelResponseText = callGemini(instruction)
            val recs = parseRecommendations(modelResponseText)

            val candidateById = candidates.associateBy { it.id }
            val resolved = recs.asSequence()
                .mapNotNull { r ->
                    val novel = candidateById[r.novelId] ?: novelRepository.findById(r.novelId).orElse(null)
                    novel?.let { RecommendedNovelDto(NovelDto.fromEntity(it), r.score.coerceIn(0.0, 1.0), r.reason) }
                }
                .filter { it.novel.id.isNotBlank() }
                .take(safeLimit)
                .toList()

            if (resolved.isEmpty()) fallback("", safeLimit) else resolved
        } catch (e: Exception) {
            fallback("", safeLimit)
        }
    }

    private fun fallback(userId: String, limit: Int): List<RecommendedNovelDto> {
        val interacted = userNovelInteractionRepository.findByUserId(userId)
            .map { it.novelId }
            .toSet()

        val pool = mutableListOf<Novel>()
        pool += novelRepository.findTop10ByOrderByRatingDesc().filter { !interacted.contains(it.id) }
        if (pool.size < limit) pool += novelRepository.findTop10ByOrderByFollowCountDesc().filter { !interacted.contains(it.id) }
        if (pool.size < limit) pool += novelRepository.findTop10ByOrderByViewCountDesc().filter { !interacted.contains(it.id) }

        // Deduplicate while preserving order
        val unique = LinkedHashMap<String, Novel>()
        for (n in pool) {
            val id = n.id ?: continue
            if (!unique.containsKey(id)) unique[id] = n
            if (unique.size >= limit) break
        }

        return unique.values
            .take(limit)
            .map {
                RecommendedNovelDto(
                    novel = NovelDto.fromEntity(it),
                    score = 0.5,
                    reason = "Fallback: popular among users (rating/follows/views)"
                )
            }
    }
}
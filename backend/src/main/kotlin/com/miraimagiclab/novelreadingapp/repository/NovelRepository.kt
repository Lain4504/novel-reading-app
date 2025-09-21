package com.miraimagiclab.novelreadingapp.repository

import com.miraimagiclab.novelreadingapp.enumeration.CategoryEnum
import com.miraimagiclab.novelreadingapp.model.Novel
import com.miraimagiclab.novelreadingapp.enumeration.NovelStatusEnum
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface NovelRepository : MongoRepository<Novel, String> {
    
    // Find by title (case insensitive)
    fun findByTitleContainingIgnoreCase(title: String, pageable: Pageable): Page<Novel>
    
    // Find by author name (case insensitive)
    fun findByAuthorNameContainingIgnoreCase(authorName: String, pageable: Pageable): Page<Novel>
    
    // Find by categories
    fun findByCategoriesIn(categories: Set<CategoryEnum>, pageable: Pageable): Page<Novel>
    
    // Find by status
    fun findByStatus(status: NovelStatusEnum, pageable: Pageable): Page<Novel>
    
    // Find by author ID
    fun findByAuthorId(authorId: String, pageable: Pageable): Page<Novel>
    
    // Find by R18 status
    fun findByIsR18(isR18: Boolean, pageable: Pageable): Page<Novel>
    
    // Find by rating range
    fun findByRatingBetween(minRating: Double, maxRating: Double, pageable: Pageable): Page<Novel>
    
    // Find by multiple criteria
    @Query("{ \$and: [" +
            "{ \$or: [{ 'title': { \$regex: ?0, \$options: 'i' } }, { \$expr: { \$eq: [?0, null] } }] }," +
            "{ \$or: [{ 'authorName': { \$regex: ?1, \$options: 'i' } }, { \$expr: { \$eq: [?1, null] } }] }," +
            "{ \$or: [{ 'categories': { \$in: ?2 } }, { \$expr: { \$eq: [?2, null] } }] }," +
            "{ \$or: [{ 'status': ?3 }, { \$expr: { \$eq: [?3, null] } }] }," +
            "{ \$or: [{ 'rating': { \$gte: ?4 } }, { \$expr: { \$eq: [?4, null] } }] }," +
            "{ \$or: [{ 'rating': { \$lte: ?5 } }, { \$expr: { \$eq: [?5, null] } }] }," +
            "{ \$or: [{ 'isR18': ?6 }, { \$expr: { \$eq: [?6, null] } }] }" +
            "] }")
    fun findByMultipleCriteria(
        title: String?,
        authorName: String?,
        categories: Set<CategoryEnum>?,
        status: NovelStatusEnum?,
        minRating: Double?,
        maxRating: Double?,
        isR18: Boolean?,
        pageable: Pageable
    ): Page<Novel>
    
    // Find top novels by view count
    fun findTop10ByOrderByViewCountDesc(): List<Novel>
    
    // Find top novels by follow count
    fun findTop10ByOrderByFollowCountDesc(): List<Novel>
    
    // Find top novels by rating
    fun findTop10ByOrderByRatingDesc(): List<Novel>
    
    // Find recently updated novels
    fun findTop10ByOrderByUpdatedAtDesc(): List<Novel>
    
    // Count by status
    fun countByStatus(status: NovelStatusEnum): Long
    
    // Count by author
    fun countByAuthorId(authorId: String): Long
    
    // Check if novel exists by title and author
    fun existsByTitleAndAuthorName(title: String, authorName: String): Boolean
}

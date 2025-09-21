package com.miraimagiclab.novelreadingapp.repository

import com.miraimagiclab.novelreadingapp.model.UserNovelInteraction
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserNovelInteractionRepository : MongoRepository<UserNovelInteraction, String> {

    fun findByUserIdAndNovelId(userId: String, novelId: String): Optional<UserNovelInteraction>

    fun findByUserId(userId: String): List<UserNovelInteraction>

    fun findByNovelId(novelId: String): List<UserNovelInteraction>

    fun findByUserIdAndHasFollowing(userId: String, hasFollowing: Boolean): List<UserNovelInteraction>

    fun findByUserIdAndInWishlist(userId: String, inWishlist: Boolean): List<UserNovelInteraction>

    fun countByNovelIdAndHasFollowing(novelId: String, hasFollowing: Boolean): Long

    fun existsByUserIdAndNovelId(userId: String, novelId: String): Boolean

    fun deleteByUserIdAndNovelId(userId: String, novelId: String)
}
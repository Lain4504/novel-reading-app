package com.miraimagiclab.novelreadingapp.repository

import com.miraimagiclab.novelreadingapp.model.Image
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ImageRepository : MongoRepository<Image, String> {
    fun findByOwnerIdAndOwnerType(ownerId: String, ownerType: String): List<Image>
}

package com.miraimagiclab.novelreadingapp.repository

import com.miraimagiclab.novelreadingapp.model.Image
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ImageRepository : MongoRepository<Image, String> {
    fun findByOwnerId(ownerId: String, pageable: Pageable): Page<Image>
    fun existsByStorageKey(storageKey: String): Boolean
}

package com.miraimagiclab.novelreadingapp.repository

import com.miraimagiclab.novelreadingapp.model.DeviceToken
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DeviceTokenRepository : MongoRepository<DeviceToken, String> {
    
    fun findByUserId(userId: String): List<DeviceToken>
    
    fun findByToken(token: String): Optional<DeviceToken>
    
    fun findByUserIdAndToken(userId: String, token: String): Optional<DeviceToken>
    
    fun deleteByToken(token: String)
    
    fun deleteByUserId(userId: String)
}


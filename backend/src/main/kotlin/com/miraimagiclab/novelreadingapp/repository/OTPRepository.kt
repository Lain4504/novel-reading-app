package com.miraimagiclab.novelreadingapp.repository

import com.miraimagiclab.novelreadingapp.model.OTP
import com.miraimagiclab.novelreadingapp.model.OTPType
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface OTPRepository : MongoRepository<OTP, String> {
    fun findByEmailAndCodeAndType(email: String, code: String, type: OTPType): OTP?
}

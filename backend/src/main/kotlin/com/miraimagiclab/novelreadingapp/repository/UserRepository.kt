package com.miraimagiclab.novelreadingapp.repository

import com.miraimagiclab.novelreadingapp.enumeration.UserRoleEnum
import com.miraimagiclab.novelreadingapp.enumeration.UserStatusEnum
import com.miraimagiclab.novelreadingapp.model.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : MongoRepository<User, String> {

    fun findByUsername(username: String): Optional<User>

    fun findByEmail(email: String): Optional<User>

    fun existsByUsername(username: String): Boolean

    fun existsByEmail(email: String): Boolean

    fun findByRolesContaining(role: UserRoleEnum): List<User>

    fun findByStatus(status: UserStatusEnum): List<User>

    fun countByStatus(status: UserStatusEnum): Long

    fun countByRolesContaining(role: UserRoleEnum): Long

    fun findByVerificationToken(verificationToken: String): User?
}
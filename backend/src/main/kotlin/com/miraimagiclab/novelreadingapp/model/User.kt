package com.miraimagiclab.novelreadingapp.model

import com.miraimagiclab.novelreadingapp.enumeration.UserRoleEnum
import com.miraimagiclab.novelreadingapp.enumeration.UserStatusEnum
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.index.TextIndexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "users")
data class User(
    @Id
    val id: String? = null,

    @Indexed
    @TextIndexed
    val username: String,

    @Indexed(unique = true)
    @TextIndexed
    val email: String,

    val password: String,

    @Indexed
    val roles: Set<UserRoleEnum> = setOf(UserRoleEnum.USER),

    @Indexed
    val status: UserStatusEnum = UserStatusEnum.ACTIVE,

    val avatarUrl: String? = null,

    val backgroundUrl: String? = null,

    val authorName: String? = null,

    // Bio của người dùng
    val bio: String? = null,

    // Public-facing name; defaults to username
    val displayName: String? = null,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    val updatedAt: LocalDateTime = LocalDateTime.now(),

    // Track when displayName was last changed to enforce 30-day rule
    val lastDisplayNameChangedAt: LocalDateTime? = null
)
package com.miraimagiclab.novelreadingapp.dto.response

import com.miraimagiclab.novelreadingapp.enumeration.UserRoleEnum
import com.miraimagiclab.novelreadingapp.enumeration.UserStatusEnum
import com.miraimagiclab.novelreadingapp.model.User
import java.time.LocalDateTime

data class UserDto(
    val id: String,
    val username: String,
    val email: String,
    val roles: Set<UserRoleEnum>,
    val status: UserStatusEnum,
    val avatarUrl: String?,
    val backgroundUrl: String?,
    val authorName: String?,
    val bio: String?,
    val displayName: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val lastDisplayNameChangedAt: LocalDateTime?
) {
    companion object {
        fun fromEntity(user: User): UserDto {
            return UserDto(
                id = user.id ?: "",
                username = user.username,
                email = user.email,
                roles = user.roles,
                status = user.status,
                avatarUrl = user.avatarUrl,
                backgroundUrl = user.backgroundUrl,
                authorName = user.authorName,
                bio = user.bio,
                displayName = user.displayName ?: user.username,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt,
                lastDisplayNameChangedAt = user.lastDisplayNameChangedAt
            )
        }
    }
}
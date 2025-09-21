package com.miraimagiclab.novelreadingapp.dto.request

import com.miraimagiclab.novelreadingapp.enumeration.UserRoleEnum
import com.miraimagiclab.novelreadingapp.enumeration.UserStatusEnum
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class UserUpdateRequest(
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    val username: String? = null,

    @field:Email(message = "Email should be valid")
    val email: String? = null,

    @field:Size(min = 6, message = "Password must be at least 6 characters")
    val password: String? = null,

    val roles: Set<UserRoleEnum>? = null,

    val status: UserStatusEnum? = null,

    val avatarUrl: String? = null,

    val backgroundUrl: String? = null,

    val authorName: String? = null,

    val bio: String? = null,

    val displayName: String? = null
)
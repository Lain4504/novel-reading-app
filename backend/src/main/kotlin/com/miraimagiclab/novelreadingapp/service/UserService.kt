package com.miraimagiclab.novelreadingapp.service

import com.miraimagiclab.novelreadingapp.dto.request.UserCreateRequest
import com.miraimagiclab.novelreadingapp.dto.request.UserUpdateRequest
import com.miraimagiclab.novelreadingapp.dto.response.UserDto
import com.miraimagiclab.novelreadingapp.exception.DuplicateUserException
import com.miraimagiclab.novelreadingapp.exception.UserNotFoundException
import com.miraimagiclab.novelreadingapp.model.User
import com.miraimagiclab.novelreadingapp.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun createUser(request: UserCreateRequest): UserDto {
        // Check for duplicate username or email
        if (userRepository.existsByUsername(request.username)) {
            throw DuplicateUserException("Username '${request.username}' already exists")
        }
        if (userRepository.existsByEmail(request.email)) {
            throw DuplicateUserException("Email '${request.email}' already exists")
        }

        val user = User(
            username = request.username,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            roles = request.roles,
            status = request.status,
            avatarUrl = request.avatarUrl,
            backgroundUrl = request.backgroundUrl,
            authorName = request.authorName,
            bio = request.bio,
            displayName = request.displayName ?: request.username,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val savedUser = userRepository.save(user)
        return UserDto.fromEntity(savedUser)
    }

    @Transactional(readOnly = true)
    fun getUserById(id: String): UserDto {
        val user = userRepository.findById(id)
            .orElseThrow { UserNotFoundException("User with ID '$id' not found") }
        return UserDto.fromEntity(user)
    }

    @Transactional(readOnly = true)
    fun getUserByUsername(username: String): UserDto {
        val user = userRepository.findByUsername(username)
            .orElseThrow { UserNotFoundException("User with username '$username' not found") }
        return UserDto.fromEntity(user)
    }

    @Transactional(readOnly = true)
    fun getUserByEmail(email: String): UserDto {
        val user = userRepository.findByEmail(email)
            .orElseThrow { UserNotFoundException("User with email '$email' not found") }
        return UserDto.fromEntity(user)
    }

    fun updateUser(id: String, request: UserUpdateRequest): UserDto {
        val existingUser = userRepository.findById(id)
            .orElseThrow { UserNotFoundException("User with ID '$id' not found") }

        // Check for duplicate username or email if being updated
        request.username?.let { username ->
            if (userRepository.existsByUsername(username) && existingUser.username != username) {
                throw DuplicateUserException("Username '$username' already exists")
            }
        }
        request.email?.let { email ->
            if (userRepository.existsByEmail(email) && existingUser.email != email) {
                throw DuplicateUserException("Email '$email' already exists")
            }
        }

        val updatedUser = existingUser.copy(
            username = request.username ?: existingUser.username,
            email = request.email ?: existingUser.email,
            password = request.password?.let { passwordEncoder.encode(it) } ?: existingUser.password,
            roles = request.roles ?: existingUser.roles,
            status = request.status ?: existingUser.status,
            avatarUrl = request.avatarUrl ?: existingUser.avatarUrl,
            backgroundUrl = request.backgroundUrl ?: existingUser.backgroundUrl,
            authorName = request.authorName ?: existingUser.authorName,
            bio = request.bio ?: existingUser.bio,
            displayName = request.displayName ?: existingUser.displayName,
            updatedAt = LocalDateTime.now()
        )

        val savedUser = userRepository.save(updatedUser)
        return UserDto.fromEntity(savedUser)
    }

    fun deleteUser(id: String) {
        if (!userRepository.existsById(id)) {
            throw UserNotFoundException("User with ID '$id' not found")
        }
        userRepository.deleteById(id)
    }

    fun updateLastLogin(id: String): UserDto {
        val user = userRepository.findById(id)
            .orElseThrow { UserNotFoundException("User with ID '$id' not found") }

        val updatedUser = user.copy(
            lastTimeLogin = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val savedUser = userRepository.save(updatedUser)
        return UserDto.fromEntity(savedUser)
    }
}
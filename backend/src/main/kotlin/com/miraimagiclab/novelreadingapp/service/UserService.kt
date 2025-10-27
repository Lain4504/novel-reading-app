package com.miraimagiclab.novelreadingapp.service

import com.miraimagiclab.novelreadingapp.config.JwtUtil
import com.miraimagiclab.novelreadingapp.dto.request.UserCreateRequest
import com.miraimagiclab.novelreadingapp.dto.request.UserUpdateRequest
import com.miraimagiclab.novelreadingapp.dto.response.UserDto
import com.miraimagiclab.novelreadingapp.enumeration.UserRoleEnum
import com.miraimagiclab.novelreadingapp.enumeration.UserStatusEnum
import com.miraimagiclab.novelreadingapp.exception.DuplicateUserException
import com.miraimagiclab.novelreadingapp.exception.UserNotFoundException
import com.miraimagiclab.novelreadingapp.model.OTPType
import com.miraimagiclab.novelreadingapp.model.User
import com.miraimagiclab.novelreadingapp.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

data class AuthResult(
    val user: User,
    val token: String,
    val refreshToken: String
)

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil,
    private val emailService: EmailService,
    private val otpService: OTPService
) {

    fun createUser(request: UserCreateRequest): UserDto {
        // Check for duplicate username or email
        if (userRepository.existsByUsername(request.username)) {
            throw DuplicateUserException("Username '${request.username}' already exists")
        }
        if (userRepository.existsByEmail(request.email)) {
            throw DuplicateUserException("Email '${request.email}' already exists")
        }

        // Generate verification token
        val verificationToken = UUID.randomUUID().toString()
        val tokenExpiresAt = LocalDateTime.now().plusHours(24) // 24 hours expiry

        val user = User(
            username = request.username,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            roles = request.roles ?: setOf(UserRoleEnum.USER),
            status = UserStatusEnum.INACTIVE, // Set status to INACTIVE until verified
            avatarUrl = request.avatarUrl,
            backgroundUrl = request.backgroundUrl,
            authorName = request.authorName,
            bio = request.bio,
            displayName = request.displayName ?: request.username,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            verificationToken = verificationToken,
            verificationTokenExpiresAt = tokenExpiresAt
        )

        val savedUser = userRepository.save(user)

        // Send account verification OTP instead of email link
        try {
            val otp = otpService.generateOTP(savedUser.email, OTPType.ACCOUNT_VERIFICATION)
            println("DEBUG: Generated OTP for ${savedUser.email}: ${otp.code}")
        } catch (e: Exception) {
            // Log the error but don't fail registration
            // In a real app, you'd use a logger
            println("Failed to send verification OTP: ${e.message}")
            e.printStackTrace()
        }

        return UserDto.fromEntity(savedUser)
    }

    @Transactional(readOnly = true)
    fun getAllUsers(page: Int, size: Int): org.springframework.data.domain.Page<UserDto> {
        val pageable = org.springframework.data.domain.PageRequest.of(
            page, 
            size, 
            org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt")
        )
        val usersPage = userRepository.findAll(pageable)
        return usersPage.map { UserDto.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun getUserById(id: String): UserDto {
        val user = userRepository.findById(id)
            .orElseThrow { UserNotFoundException("User with ID '$id' not found") }
        return UserDto.fromEntity(user)
    }

    @Transactional(readOnly = true)
    fun getUserEntityById(id: String): User {
        return userRepository.findById(id)
            .orElseThrow { UserNotFoundException("User with ID '$id' not found") }
    }

    @Transactional(readOnly = true)
    fun getUserByUsername(username: String): UserDto {
        val user = userRepository.findByUsername(username)
            .orElseThrow { UserNotFoundException("User with username '$username' not found") }
        return UserDto.fromEntity(user)
    }

    @Transactional(readOnly = true)
    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email).orElse(null)
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

    fun authenticate(usernameOrEmail: String, password: String): AuthResult {
        val user = if (usernameOrEmail.contains("@")) {
            userRepository.findByEmail(usernameOrEmail)
        } else {
            userRepository.findByUsername(usernameOrEmail)
        }.orElseThrow { UserNotFoundException("Invalid username/email or password") }

        if (!passwordEncoder.matches(password, user.password)) {
            throw UserNotFoundException("Invalid username/email or password")
        }

        // Check if account is active
        if (user.status != UserStatusEnum.ACTIVE) {
            throw UserNotFoundException("Account is not activated. Please verify your email first.")
        }

        val token = jwtUtil.generateToken(user)
        val refreshToken = jwtUtil.generateRefreshToken(user)

        return AuthResult(user, token, refreshToken)
    }

    fun resetPassword(email: String, newPassword: String) {
        val user = userRepository.findByEmail(email)
            .orElseThrow { UserNotFoundException("User with email '$email' not found") }

        val updatedUser = user.copy(
            password = passwordEncoder.encode(newPassword),
            updatedAt = LocalDateTime.now()
        )

        userRepository.save(updatedUser)
    }

    fun activateAccount(email: String) {
        val user = userRepository.findByEmail(email)
            .orElseThrow { UserNotFoundException("User with email '$email' not found") }

        val updatedUser = user.copy(
            status = UserStatusEnum.ACTIVE,
            updatedAt = LocalDateTime.now()
        )

        val savedUser = userRepository.save(updatedUser)
        println("DEBUG: Account activated for email $email, new status: ${savedUser.status}")
    }

    fun activateAccountByOTP(email: String, otpCode: String): Boolean {
        // First check if user exists
        val user = userRepository.findByEmail(email).orElse(null)
            ?: throw UserNotFoundException("User with email '$email' not found")

        // Verify OTP first
        val isValidOTP = otpService.verifyOTP(email, otpCode, OTPType.ACCOUNT_VERIFICATION)
        if (!isValidOTP) {
            return false
        }

        // Activate account
        activateAccount(email)

        // Verify the account was actually activated
        val activatedUser = userRepository.findByEmail(email).orElse(null)
        return activatedUser?.status == UserStatusEnum.ACTIVE
    }

    fun findByVerificationToken(token: String): User? {
        return userRepository.findByVerificationToken(token)
    }

    fun activateAccountByToken(token: String) {
        val user = userRepository.findByVerificationToken(token)
            ?: throw UserNotFoundException("Invalid verification token")

        val updatedUser = user.copy(
            status = UserStatusEnum.ACTIVE,
            verificationToken = null, // Clear the token after use
            verificationTokenExpiresAt = null,
            updatedAt = LocalDateTime.now()
        )

        userRepository.save(updatedUser)
    }

    fun updateVerificationToken(email: String, newToken: String, expiresAt: LocalDateTime) {
        val user = userRepository.findByEmail(email)
            .orElseThrow { UserNotFoundException("User with email '$email' not found") }

        val updatedUser = user.copy(
            verificationToken = newToken,
            verificationTokenExpiresAt = expiresAt,
            updatedAt = LocalDateTime.now()
        )

        userRepository.save(updatedUser)
    }

    fun upgradeToAuthor(userId: String): AuthResult {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User with ID '$userId' not found") }

        // Add AUTHOR role to existing roles
        val updatedRoles = user.roles.toMutableSet().apply {
            add(UserRoleEnum.AUTHOR)
        }

        val updatedUser = user.copy(
            roles = updatedRoles,
            updatedAt = LocalDateTime.now()
        )

        val savedUser = userRepository.save(updatedUser)

        // Generate new JWT tokens with updated roles
        val token = jwtUtil.generateToken(savedUser)
        val refreshToken = jwtUtil.generateRefreshToken(savedUser)

        return AuthResult(savedUser, token, refreshToken)
    }

}
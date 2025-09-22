package com.miraimagiclab.novelreadingapp.controller

import com.miraimagiclab.novelreadingapp.config.JwtUtil
import com.miraimagiclab.novelreadingapp.dto.ApiResponse
import com.miraimagiclab.novelreadingapp.dto.request.LoginRequest
import com.miraimagiclab.novelreadingapp.dto.request.UserCreateRequest
import com.miraimagiclab.novelreadingapp.dto.request.UserUpdateRequest
import com.miraimagiclab.novelreadingapp.dto.response.LoginResponse
import com.miraimagiclab.novelreadingapp.dto.response.UserDto
import com.miraimagiclab.novelreadingapp.service.UserService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = ["http://localhost:3000", "http://localhost:8080", "http://127.0.0.1:3000", "http://127.0.0.1:8080"])
@Tag(name = "User Management", description = "APIs for managing users")
class UserController(
    private val userService: UserService,
    private val jwtUtil: JwtUtil
) {

    @PostMapping
    fun createUser(@Valid @RequestBody request: UserCreateRequest): ResponseEntity<ApiResponse<UserDto>> {
        val user = userService.createUser(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(user, "User created successfully"))
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<ApiResponse<LoginResponse>> {
        val user = userService.authenticate(request.usernameOrEmail, request.password)
        val token = jwtUtil.generateToken(user)
        val userDto = UserDto.fromEntity(user)
        val loginResponse = LoginResponse(token, userDto)
        return ResponseEntity.ok(ApiResponse.success(loginResponse, "Login successful"))
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: String): ResponseEntity<ApiResponse<UserDto>> {
        val user = userService.getUserById(id)
        return ResponseEntity.ok(ApiResponse.success(user, "User retrieved successfully"))
    }

    @GetMapping("/by-username/{username}")
    fun getUserByUsername(@PathVariable username: String): ResponseEntity<ApiResponse<UserDto>> {
        val user = userService.getUserByUsername(username)
        return ResponseEntity.ok(ApiResponse.success(user, "User retrieved successfully"))
    }

    @GetMapping("/by-email/{email}")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<ApiResponse<UserDto>> {
        val user = userService.getUserByEmail(email)
        return ResponseEntity.ok(ApiResponse.success(user, "User retrieved successfully"))
    }

    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: String,
        @Valid @RequestBody request: UserUpdateRequest
    ): ResponseEntity<ApiResponse<UserDto>> {
        val user = userService.updateUser(id, request)
        return ResponseEntity.ok(ApiResponse.success(user, "User updated successfully"))
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: String): ResponseEntity<ApiResponse<Nothing>> {
        userService.deleteUser(id)
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"))
    }

}
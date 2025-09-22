package com.miraimagiclab.novelreadingapp.config

import com.miraimagiclab.novelreadingapp.model.User
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtUtil {

    @Value("\${jwt.secret:defaultSecretKeyThatIsAtLeast32CharactersLong}")
    private lateinit var secret: String

    @Value("\${jwt.expiration:86400000}") // 24 hours in milliseconds
    private var jwtExpiration: Long = 86400000

    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray())
    }

    fun generateToken(user: User): String {
        val claims = mutableMapOf<String, Any>()
        claims["userId"] = user.id!!
        claims["username"] = user.username
        claims["email"] = user.email
        claims["roles"] = user.roles.map { it.name }

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(user.username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(key)
            .compact()
    }

    fun getUsernameFromToken(token: String): String {
        return getClaimsFromToken(token).subject
    }

    fun getUserIdFromToken(token: String): String {
        return getClaimsFromToken(token)["userId"] as String
    }

    fun getRolesFromToken(token: String): List<String> {
        val roles = getClaimsFromToken(token)["roles"] as? List<*>
        return roles?.map { it.toString() } ?: emptyList()
    }

    fun isTokenExpired(token: String): Boolean {
        return getClaimsFromToken(token).expiration.before(Date())
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username = getUsernameFromToken(token)
        return username == userDetails.username && !isTokenExpired(token)
    }

    private fun getClaimsFromToken(token: String): Claims {
        return Jwts.parser()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }
}
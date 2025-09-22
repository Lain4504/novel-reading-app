package com.miraimagiclab.novelreadingapp.config

import com.miraimagiclab.novelreadingapp.enumeration.UserRoleEnum
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7)
            try {
                if (!jwtUtil.isTokenExpired(token)) {
                    val username = jwtUtil.getUsernameFromToken(token)
                    val roles = jwtUtil.getRolesFromToken(token)

                    val authorities = roles.map { SimpleGrantedAuthority("ROLE_$it") }

                    val authentication = UsernamePasswordAuthenticationToken(
                        username, null, authorities
                    ).apply {
                        details = WebAuthenticationDetailsSource().buildDetails(request)
                    }

                    SecurityContextHolder.getContext().authentication = authentication
                }
            } catch (e: Exception) {
                // Invalid token, continue without authentication
                logger.warn("Invalid JWT token: ${e.message}")
            }
        }

        filterChain.doFilter(request, response)
    }
}
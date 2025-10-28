package com.miraimagiclab.novelreadingapp.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val corsConfigurationSource: CorsConfigurationSource
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource) }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { authz ->
                authz
                    .requestMatchers("/health", "/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/index.html", "/swagger-ui/").permitAll()
                    .requestMatchers("/users", "/users/login", "/users/refresh").permitAll() // Allow signup and login
                    .requestMatchers("/auth/verify-email").permitAll() // Allow email verification
                    .requestMatchers("/auth/verify-account").permitAll() // Allow account verification
                    .requestMatchers("/auth/forgot-password").permitAll() // Allow forgot password
                    .requestMatchers("/auth/verify-reset-otp").permitAll() // Allow verify reset OTP
                    .requestMatchers("/auth/reset-password").permitAll() // Allow reset password
                    .requestMatchers("/auth/resend-verification").permitAll() // Allow resend verification
                    // Allow public access to read novels (GET requests only)
                    .requestMatchers(HttpMethod.GET, "/novels", "/novels/**").permitAll() // Allow GET requests to novels
                    
                    // Allow public access to search novels (POST request)
                    .requestMatchers(HttpMethod.POST, "/novels/search").permitAll() // Allow POST requests to search novels
                    
                    // Allow public access to read chapters (GET requests only)
                    .requestMatchers(HttpMethod.GET, "/chapters", "/chapters/**").permitAll() // Allow GET requests to chapters
                    
                    // Allow public access to read reviews (GET requests only)
                    .requestMatchers(HttpMethod.GET, "/reviews", "/reviews/**").permitAll() // Allow GET requests to reviews

                    // Allow public access to read comment (GET requests only)
                    .requestMatchers(HttpMethod.GET, "/comments", "/comments/**").permitAll() // Allow GET requests to comments
                    // Allow public access to read user profiles (GET requests only)
                    .requestMatchers(HttpMethod.GET, "/users/{id}", "/users/by-username/{username}").permitAll() // Allow GET requests to user profiles
                    
                    // Allow public access to read novel interactions (GET requests only)
                    .requestMatchers(HttpMethod.GET, "/interactions/novels/{novelId}/follow-count").permitAll() // Allow GET follow count

                    // ALLOW IMAGE UPLOAD AND READ WITHOUT AUTH
                    .requestMatchers(HttpMethod.POST, "/images/upload").permitAll()
                    .requestMatchers(HttpMethod.GET, "/images/**").permitAll()
                    
                    .anyRequest().authenticated() // All other requests require authentication
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
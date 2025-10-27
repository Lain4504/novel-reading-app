package com.miraimagiclab.novelreadingapp.data.auth

import com.miraimagiclab.novelreadingapp.data.local.prefs.AuthDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AuthState(
    val isLoggedIn: Boolean,
    val accessToken: String?,
    val refreshToken: String?,
    val tokenExpiration: Long?,
    val userId: String?,
    val username: String?,
    val email: String?,
    val roles: Set<String> = emptySet()
)

class SessionManager(
    private val authDataStore: AuthDataStore
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    val authState: StateFlow<AuthState> = combine(
        authDataStore.accessToken,
        authDataStore.refreshToken,
        authDataStore.tokenExpiration,
        authDataStore.userId,
        authDataStore.username,
        authDataStore.email,
        authDataStore.userRoles
    ) { flows ->
        val access = flows[0] as String?
        val refresh = flows[1] as String?
        val expiration = flows[2] as Long?
        val uid = flows[3] as String?
        val uname = flows[4] as String?
        val mail = flows[5] as String?
        val rolesString = flows[6] as String?
        val roles = rolesString?.split(",")?.toSet() ?: emptySet()
        
        AuthState(
            isLoggedIn = !access.isNullOrBlank(),
            accessToken = access,
            refreshToken = refresh,
            tokenExpiration = expiration,
            userId = uid,
            username = uname,
            email = mail,
            roles = roles
        )
    }.stateIn(scope, SharingStarted.Eagerly, AuthState(false, null, null, null, null, null, null, emptySet()))

    fun saveSession(accessToken: String, refreshToken: String, userId: String, username: String, email: String, roles: Set<String>? = null, expirationTime: Long? = null) {
        scope.launch {
            authDataStore.saveTokens(accessToken, refreshToken, expirationTime)
            authDataStore.saveUser(userId, username, email, roles)
        }
    }

    fun hasRole(role: String): Boolean {
        return authState.value.roles.contains(role)
    }

    fun clearSession() {
        scope.launch { authDataStore.clear() }
    }
}



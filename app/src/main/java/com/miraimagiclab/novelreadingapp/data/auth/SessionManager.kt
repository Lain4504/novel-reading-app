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
    val userId: String?,
    val username: String?,
    val email: String?
)

class SessionManager(
    private val authDataStore: AuthDataStore
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    val authState: StateFlow<AuthState> = combine(
        authDataStore.accessToken,
        authDataStore.refreshToken,
        authDataStore.userId,
        authDataStore.username,
        authDataStore.email
    ) { access, refresh, uid, uname, mail ->
        AuthState(
            isLoggedIn = !access.isNullOrBlank(),
            accessToken = access,
            refreshToken = refresh,
            userId = uid,
            username = uname,
            email = mail
        )
    }.stateIn(scope, SharingStarted.Eagerly, AuthState(false, null, null, null, null, null))

    fun saveSession(accessToken: String, refreshToken: String, userId: String, username: String, email: String) {
        scope.launch {
            authDataStore.saveTokens(accessToken, refreshToken)
            authDataStore.saveUser(userId, username, email)
        }
    }

    fun clearSession() {
        scope.launch { authDataStore.clear() }
    }
}



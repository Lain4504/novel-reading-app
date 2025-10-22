package com.miraimagiclab.novelreadingapp.data.local.prefs

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class AuthDataStore(private val context: Context) {

    object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val TOKEN_EXPIRATION = longPreferencesKey("token_expiration")
        val USER_ID = stringPreferencesKey("user_id")
        val USERNAME = stringPreferencesKey("username")
        val EMAIL = stringPreferencesKey("email")
        val USER_ROLES = stringPreferencesKey("user_roles")
    }

    val accessToken: Flow<String?> = context.dataStore.data.map { it[Keys.ACCESS_TOKEN] }
    val refreshToken: Flow<String?> = context.dataStore.data.map { it[Keys.REFRESH_TOKEN] }
    val tokenExpiration: Flow<Long?> = context.dataStore.data.map { it[Keys.TOKEN_EXPIRATION] }
    val userId: Flow<String?> = context.dataStore.data.map { it[Keys.USER_ID] }
    val username: Flow<String?> = context.dataStore.data.map { it[Keys.USERNAME] }
    val email: Flow<String?> = context.dataStore.data.map { it[Keys.EMAIL] }
    val userRoles: Flow<String?> = context.dataStore.data.map { it[Keys.USER_ROLES] }

    suspend fun saveTokens(accessToken: String, refreshToken: String, expirationTime: Long? = null) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ACCESS_TOKEN] = accessToken
            prefs[Keys.REFRESH_TOKEN] = refreshToken
            expirationTime?.let { prefs[Keys.TOKEN_EXPIRATION] = it }
        }
    }

    suspend fun saveUser(userId: String, username: String, email: String, roles: Set<String>? = null) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_ID] = userId
            prefs[Keys.USERNAME] = username
            prefs[Keys.EMAIL] = email
            roles?.let { prefs[Keys.USER_ROLES] = it.joinToString(",") }
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}



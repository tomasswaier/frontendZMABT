package com.example.frontendzmabt.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class SessionManager(private val context: Context
    ) {

    companion object {
        val TOKEN_KEY = stringPreferencesKey("auth_token")
        val USERNAME= stringPreferencesKey("username")
        val USERID= stringPreferencesKey("userid")
        val USEREMAIL= stringPreferencesKey("useremail")
    }

    suspend fun saveToken(token: String, username: String?, email: String?, id: Number?) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[USERNAME]= username as String
            prefs[USERID] = id.toString()
            prefs[USEREMAIL]=email as String
        }
    }

    suspend fun getToken(): String? {
        val prefs = context.dataStore.data.first()
        return prefs[TOKEN_KEY]
    }
    suspend fun getUser(): User {
        val prefs = context.dataStore.data.first()
        return User(
            id = prefs[USERID]?.toInt(),
            username = prefs[USERNAME],
            email = prefs[USEREMAIL],
            createdAt = "",
            updatedAt = "",
        )
    }

    suspend fun isLoggedIn(): Boolean {
        return getToken() != null
    }

    suspend fun logout() {
        context.dataStore.edit { it.clear() }
    }
}
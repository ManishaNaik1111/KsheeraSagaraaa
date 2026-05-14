package com.ksheera.sagaraaa.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USERNAME = stringPreferencesKey("username")
        val EMAIL = stringPreferencesKey("email")
        val PASSWORD = stringPreferencesKey("password")
        val IS_REGISTERED = booleanPreferencesKey("is_registered")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { it[IS_LOGGED_IN] ?: false }

    val username: Flow<String> = context.dataStore.data
        .map { it[USERNAME] ?: "" }

    val isRegistered: Flow<Boolean> = context.dataStore.data
        .map { it[IS_REGISTERED] ?: false }

    fun getStoredEmail(context: Context): Flow<String> =
        context.dataStore.data.map { it[EMAIL] ?: "" }

    fun getStoredPassword(context: Context): Flow<String> =
        context.dataStore.data.map { it[PASSWORD] ?: "" }

    suspend fun saveUser(username: String, email: String, password: String) {
        context.dataStore.edit { prefs ->
            prefs[USERNAME] = username
            prefs[EMAIL] = email
            prefs[PASSWORD] = password
            prefs[IS_REGISTERED] = true
            prefs[IS_LOGGED_IN] = false // Don't auto-login on register
        }
    }

    suspend fun setLoggedIn(value: Boolean) {
        context.dataStore.edit { it[IS_LOGGED_IN] = value }
    }

    suspend fun logout(context: Context) {
        context.dataStore.edit { it[IS_LOGGED_IN] = false }
    }
}
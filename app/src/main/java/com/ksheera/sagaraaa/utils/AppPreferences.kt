package com.ksheera.sagaraaa.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_prefs")

class AppPreferences(private val context: Context) {

    companion object {
        val LANGUAGE_KEY = stringPreferencesKey("language")
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        val THEME_KEY = stringPreferencesKey("theme")
        val ONBOARDING_KEY = booleanPreferencesKey("onboarding_done")
        val GEMINI_KEY = stringPreferencesKey("gemini_api_key")
    }

    val language: Flow<String> = context.dataStore.data.map {
        it[LANGUAGE_KEY] ?: LanguageManager.ENGLISH
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map {
        it[DARK_MODE_KEY] ?: false
    }

    val theme: Flow<String> = context.dataStore.data.map {
        it[THEME_KEY] ?: "Green"
    }

    val isOnboardingDone: Flow<Boolean> = context.dataStore.data.map {
        it[ONBOARDING_KEY] ?: false
    }

    val geminiApiKey: Flow<String> = context.dataStore.data.map {
        it[GEMINI_KEY] ?: ""
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { it[LANGUAGE_KEY] = lang }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[DARK_MODE_KEY] = enabled }
    }

    suspend fun setTheme(theme: String) {
        context.dataStore.edit { it[THEME_KEY] = theme }
    }

    suspend fun setOnboardingDone(done: Boolean) {
        context.dataStore.edit { it[ONBOARDING_KEY] = done }
    }

    suspend fun setGeminiApiKey(key: String) {
        context.dataStore.edit { it[GEMINI_KEY] = key }
    }
}
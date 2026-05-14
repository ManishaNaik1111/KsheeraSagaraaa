package com.ksheera.sagaraaa.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ksheera.sagaraaa.data.dataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

enum class AppTheme(val displayName: String) {
    GREEN("🌿 Green"),
    BLUE("💙 Blue"),
    ORANGE("🧡 Orange")
}

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    private val THEME_KEY = stringPreferencesKey("app_theme")
    private val ONBOARDING_KEY = booleanPreferencesKey("onboarding_done")

    var isDarkMode by mutableStateOf(false)
        private set

    var currentTheme by mutableStateOf(AppTheme.GREEN)
        private set

    var onboardingDone by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            getApplication<Application>().dataStore.data.map {
                Triple(
                    it[DARK_MODE_KEY] ?: false,
                    it[THEME_KEY] ?: "GREEN",
                    it[ONBOARDING_KEY] ?: false
                )
            }.collect { (dark, theme, onboarding) ->
                isDarkMode = dark
                currentTheme = try {
                    AppTheme.valueOf(theme)
                } catch (e: Exception) { AppTheme.GREEN }
                onboardingDone = onboarding
            }
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit {
                it[DARK_MODE_KEY] = !isDarkMode
            }
        }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit {
                it[THEME_KEY] = theme.name
            }
        }
    }

    fun setOnboardingDone() {
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit {
                it[ONBOARDING_KEY] = true
            }
        }
    }
}
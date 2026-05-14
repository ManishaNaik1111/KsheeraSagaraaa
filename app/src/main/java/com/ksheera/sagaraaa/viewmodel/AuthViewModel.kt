package com.ksheera.sagaraaa.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ksheera.sagaraaa.data.UserPreferences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = UserPreferences(application)

    val isLoggedIn = prefs.isLoggedIn.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    val username = prefs.username.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), ""
    )

    val isRegistered = prefs.isRegistered.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    private val _authMessage = MutableStateFlow("")
    val authMessage: StateFlow<String> = _authMessage

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                _authMessage.value = "Please fill all fields!"
                return@launch
            }
            if (!email.contains("@")) {
                _authMessage.value = "Enter a valid email!"
                return@launch
            }
            if (password.length < 6) {
                _authMessage.value = "Password must be at least 6 characters!"
                return@launch
            }
            // Save user but DO NOT auto-login
            // User must login manually after registration
            prefs.saveUser(username, email, password)
            _authMessage.value = "REGISTERED_SUCCESS"
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            if (email.isEmpty() || password.isEmpty()) {
                _authMessage.value = "Please enter email and password!"
                return@launch
            }

            // Check if registered
            val registered = prefs.isRegistered.first()
            if (!registered) {
                _authMessage.value = "NO_ACCOUNT"
                return@launch
            }

            // Get stored credentials
            val storedEmail = prefs.getStoredEmail(getApplication()).first()
            val storedPassword = prefs.getStoredPassword(getApplication()).first()

            if (storedEmail.isEmpty()) {
                _authMessage.value = "NO_ACCOUNT"
                return@launch
            }

            if (storedEmail.lowercase() == email.trim().lowercase() && storedPassword == password) {
                prefs.setLoggedIn(true)
                _authMessage.value = "LOGIN_SUCCESS"
            } else {
                _authMessage.value = "Invalid email or password! Please try again."
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            prefs.logout(getApplication())
            _authMessage.value = ""
        }
    }

    fun clearMessage() {
        _authMessage.value = ""
    }
}
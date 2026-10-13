package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserProfile(
    val username: String = "Senpai",
    val isSignedIn: Boolean = false,
    val avatarId: Int = 1,
    val byokApiKey: String = "",
    val isOnboardingComplete: Boolean = false,
    val isSafeMode: Boolean = true,
    val affinityLevel: Int = 1
)

class UserPreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("konaneko_prefs", Context.MODE_PRIVATE)

    private val _userProfile = MutableStateFlow(loadProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private fun loadProfile(): UserProfile {
        return UserProfile(
            username = prefs.getString("username", "Senpai") ?: "Senpai",
            isSignedIn = prefs.getBoolean("is_signed_in", false),
            avatarId = prefs.getInt("avatar_id", 1),
            byokApiKey = prefs.getString("byok_api_key", "") ?: "",
            isOnboardingComplete = prefs.getBoolean("is_onboarding_complete", false),
            isSafeMode = prefs.getBoolean("is_safe_mode", true),
            affinityLevel = prefs.getInt("affinity_level", 1)
        )
    }

    fun setByokApiKey(key: String) {
        prefs.edit().putString("byok_api_key", key).apply()
        _userProfile.value = _userProfile.value.copy(byokApiKey = key)
    }

    fun completeOnboarding() {
        prefs.edit().putBoolean("is_onboarding_complete", true).apply()
        _userProfile.value = _userProfile.value.copy(isOnboardingComplete = true)
    }

    fun signIn(username: String, avatarId: Int) {
        prefs.edit()
            .putString("username", username)
            .putInt("avatar_id", avatarId)
            .putBoolean("is_signed_in", true)
            .apply()
        _userProfile.value = _userProfile.value.copy(
            username = username,
            avatarId = avatarId,
            isSignedIn = true
        )
    }

    fun signOut() {
        prefs.edit()
            .putString("username", "Senpai")
            .putBoolean("is_signed_in", false)
            .apply()
        _userProfile.value = _userProfile.value.copy(
            username = "Senpai",
            isSignedIn = false
        )
    }

    fun updateSafeMode(enabled: Boolean) {
        prefs.edit().putBoolean("is_safe_mode", enabled).apply()
        _userProfile.value = _userProfile.value.copy(isSafeMode = enabled)
    }

    fun incrementAffinity() {
        val current = _userProfile.value.affinityLevel
        if (current < 5) {
            val next = current + 1
            prefs.edit().putInt("affinity_level", next).apply()
            _userProfile.value = _userProfile.value.copy(affinityLevel = next)
        }
    }
}

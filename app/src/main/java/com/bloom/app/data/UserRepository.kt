package com.bloom.app.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserRepository(private val prefs: OnboardingPreferences) {

    // Cached state flows read from DataStore
    val isOnboardingComplete: Flow<Boolean> = prefs.isOnboardingComplete
    val userName: Flow<String>              = prefs.userName
    val reminderTime: Flow<String>          = prefs.reminderTime

    // In-memory cache updated on first read — used by AppOpenAdManager (non-blocking)
    private val _onboardingCompletedCache = MutableStateFlow(false)
    val onboardingCompletedCache = _onboardingCompletedCache.asStateFlow()

    fun updateOnboardingCache(value: Boolean) {
        _onboardingCompletedCache.value = value
    }

    suspend fun completeOnboarding(name: String, reminderTime: String) {
        prefs.setUserName(name)
        prefs.setReminderTime(reminderTime)
        prefs.setOnboardingComplete(true)
        _onboardingCompletedCache.value = true
    }

    suspend fun setUserName(name: String) {
        prefs.setUserName(name)
    }
}

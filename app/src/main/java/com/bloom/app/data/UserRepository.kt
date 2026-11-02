package com.bloom.app.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserRepository(private val prefs: OnboardingPreferences) {

    // Cached state flows read from DataStore
    val isOnboardingComplete: Flow<Boolean> = prefs.isOnboardingComplete
    val userName: Flow<String>              = prefs.userName
    val reminderTime: Flow<String>          = prefs.reminderTime

    // Goal progress flows
    val lastProgressDate: Flow<String?>     = prefs.lastProgressDate
    val goalMeals: Flow<Int>                = prefs.goalMeals
    val goalMovement: Flow<Int>             = prefs.goalMovement
    val goalWater: Flow<Int>                = prefs.goalWater
    val goalRelaxation: Flow<Int>           = prefs.goalRelaxation
    val goalSleep: Flow<Int>                = prefs.goalSleep
    val tipVisible: Flow<Boolean>           = prefs.tipVisible

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

    suspend fun saveGoalProgress(
        date: String,
        meals: Int,
        movement: Int,
        water: Int,
        relaxation: Int,
        sleep: Int,
        tipVisible: Boolean
    ) {
        prefs.saveGoalProgress(date, meals, movement, water, relaxation, sleep, tipVisible)
    }

    suspend fun resetDailyProgress(date: String) {
        prefs.resetDailyProgress(date)
    }
}

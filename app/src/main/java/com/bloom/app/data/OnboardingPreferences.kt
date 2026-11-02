package com.bloom.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "bloom_prefs")

class OnboardingPreferences(private val context: Context) {

    companion object {
        val KEY_ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val KEY_USER_NAME           = stringPreferencesKey("user_name")
        val KEY_REMINDER_TIME       = stringPreferencesKey("reminder_time")
        
        // Goal progress persistence
        val KEY_LAST_PROGRESS_DATE  = stringPreferencesKey("last_progress_date")
        val KEY_GOAL_MEALS          = intPreferencesKey("goal_meals")
        val KEY_GOAL_MOVEMENT       = intPreferencesKey("goal_movement")
        val KEY_GOAL_WATER          = intPreferencesKey("goal_water")
        val KEY_GOAL_RELAXATION     = intPreferencesKey("goal_relaxation")
        val KEY_GOAL_SLEEP          = intPreferencesKey("goal_sleep")
        val KEY_TIP_VISIBLE         = booleanPreferencesKey("tip_visible")
    }

    val isOnboardingComplete: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[KEY_ONBOARDING_COMPLETE] ?: false }

    val userName: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[KEY_USER_NAME] ?: "Sofia" }

    val reminderTime: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[KEY_REMINDER_TIME] ?: "08:00 AM" }

    val lastProgressDate: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[KEY_LAST_PROGRESS_DATE] }

    val goalMeals: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[KEY_GOAL_MEALS] ?: 0 }

    val goalMovement: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[KEY_GOAL_MOVEMENT] ?: 0 }

    val goalWater: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[KEY_GOAL_WATER] ?: 0 }

    val goalRelaxation: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[KEY_GOAL_RELAXATION] ?: 0 }

    val goalSleep: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[KEY_GOAL_SLEEP] ?: 0 }

    val tipVisible: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[KEY_TIP_VISIBLE] ?: true }

    suspend fun setOnboardingComplete(complete: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ONBOARDING_COMPLETE] = complete
        }
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_NAME] = name.ifBlank { "Sofia" }
        }
    }

    suspend fun setReminderTime(time: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_REMINDER_TIME] = time
        }
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
        context.dataStore.edit { prefs ->
            prefs[KEY_LAST_PROGRESS_DATE] = date
            prefs[KEY_GOAL_MEALS]         = meals
            prefs[KEY_GOAL_MOVEMENT]      = movement
            prefs[KEY_GOAL_WATER]         = water
            prefs[KEY_GOAL_RELAXATION]    = relaxation
            prefs[KEY_GOAL_SLEEP]         = sleep
            prefs[KEY_TIP_VISIBLE]        = tipVisible
        }
    }

    suspend fun resetDailyProgress(date: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LAST_PROGRESS_DATE] = date
            prefs[KEY_GOAL_MEALS]         = 0
            prefs[KEY_GOAL_MOVEMENT]      = 0
            prefs[KEY_GOAL_WATER]         = 0
            prefs[KEY_GOAL_RELAXATION]    = 0
            prefs[KEY_GOAL_SLEEP]         = 0
            prefs[KEY_TIP_VISIBLE]        = true
        }
    }
}

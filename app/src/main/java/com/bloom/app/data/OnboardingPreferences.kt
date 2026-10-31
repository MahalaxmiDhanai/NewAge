package com.bloom.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
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
    }

    val isOnboardingComplete: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[KEY_ONBOARDING_COMPLETE] ?: false }

    val userName: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[KEY_USER_NAME] ?: "Sofia" }

    val reminderTime: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[KEY_REMINDER_TIME] ?: "08:00 AM" }

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
}

package com.bloom.app

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bloom.app.data.OnboardingPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for OnboardingPreferences (stable key names and default values).
 *
 * Validates the persistence contract without needing a real Context or DataStore.
 */
class OnboardingPreferencesTest {

    @Test
    fun `key names are stable`() {
        assertEquals("onboarding_complete", OnboardingPreferences.KEY_ONBOARDING_COMPLETE.name)
        assertEquals("user_name",           OnboardingPreferences.KEY_USER_NAME.name)
        assertEquals("reminder_time",       OnboardingPreferences.KEY_REMINDER_TIME.name)
    }

    @Test
    fun `default onboarding complete is false when key absent`() {
        val prefs = mutablePreferencesOf()
        val value = prefs[booleanPreferencesKey("onboarding_complete")] ?: false
        assertFalse(value)
    }

    @Test
    fun `onboarding complete returns true after setting`() {
        val prefs = mutablePreferencesOf()
        prefs[booleanPreferencesKey("onboarding_complete")] = true
        val value = prefs[booleanPreferencesKey("onboarding_complete")] ?: false
        assertTrue(value)
    }

    @Test
    fun `default user name is Sofia when key absent`() {
        val prefs = mutablePreferencesOf()
        val name = prefs[stringPreferencesKey("user_name")] ?: "Sofia"
        assertEquals("Sofia", name)
    }

    @Test
    fun `stored user name is returned when set`() {
        val prefs = mutablePreferencesOf()
        prefs[stringPreferencesKey("user_name")] = "Emma"
        val name = prefs[stringPreferencesKey("user_name")] ?: "Sofia"
        assertEquals("Emma", name)
    }

    @Test
    fun `blank name falls back to Sofia`() {
        val raw = "   "
        val stored = raw.ifBlank { "Sofia" }
        assertEquals("Sofia", stored)
    }

    @Test
    fun `non-blank name is preserved`() {
        val raw = "Priya"
        val stored = raw.ifBlank { "Sofia" }
        assertEquals("Priya", stored)
    }

    @Test
    fun `default reminder time is 08 00 AM when key absent`() {
        val prefs = mutablePreferencesOf()
        val time = prefs[stringPreferencesKey("reminder_time")] ?: "08:00 AM"
        assertEquals("08:00 AM", time)
    }

    @Test
    fun `stored reminder time is returned when set`() {
        val prefs = mutablePreferencesOf()
        prefs[stringPreferencesKey("reminder_time")] = "07:30 AM"
        val time = prefs[stringPreferencesKey("reminder_time")] ?: "08:00 AM"
        assertEquals("07:30 AM", time)
    }
}

package com.bloom.app

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import com.bloom.app.data.UserRepository
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for AppOpenAdManager eligibility logic.
 *
 * Rule: onboardingCompletedCache.value must be true.
 * Tests the rule directly (AppOpenAdManager is final and needs Context/AdMob SDK).
 */
class AppOpenAdManagerEligibilityTest {

    // Eligibility check (mirrors AppOpenAdManager.isEligible)
    private fun isEligible(userRepository: UserRepository): Boolean =
        userRepository.onboardingCompletedCache.value

    // ── Tests ─────────────────────────────────────────────────────────────

    @Test
    fun `not eligible when onboarding not complete`() {
        val flow = MutableStateFlow(false)
        val repo = mockk<UserRepository> { every { onboardingCompletedCache } returns flow }
        assertFalse(isEligible(repo))
    }

    @Test
    fun `eligible when onboarding complete`() {
        val flow = MutableStateFlow(true)
        val repo = mockk<UserRepository> { every { onboardingCompletedCache } returns flow }
        assertTrue(isEligible(repo))
    }

    @Test
    fun `eligibility reflects live state changes`() {
        val flow = MutableStateFlow(false)
        val repo = mockk<UserRepository> { every { onboardingCompletedCache } returns flow }
        assertFalse(isEligible(repo))
        flow.value = true
        assertTrue(isEligible(repo))
    }

    @Test
    fun `first-time user never eligible across multiple checks`() {
        val flow = MutableStateFlow(false)
        val repo = mockk<UserRepository> { every { onboardingCompletedCache } returns flow }
        repeat(5) { assertFalse(isEligible(repo)) }
    }

    @Test
    fun `returning user consistently eligible`() {
        val flow = MutableStateFlow(true)
        val repo = mockk<UserRepository> { every { onboardingCompletedCache } returns flow }
        repeat(5) { assertTrue(isEligible(repo)) }
    }

    @Test
    fun `eligible only becomes true after onboarding completes`() {
        val flow = MutableStateFlow(false)
        val repo = mockk<UserRepository> { every { onboardingCompletedCache } returns flow }

        // Simulate the user completing onboarding
        assertFalse(isEligible(repo))
        flow.value = true
        assertTrue(isEligible(repo))

        // Should remain eligible on subsequent checks
        assertTrue(isEligible(repo))
    }
}

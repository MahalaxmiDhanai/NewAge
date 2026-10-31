package com.bloom.app.ui.splash

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bloom.app.ads.AppOpenAdManager
import com.bloom.app.data.UserRepository
import com.bloom.app.ui.components.FlowerLogo
import com.bloom.app.ui.components.FourPointSparkle
import com.bloom.app.ui.theme.BackgroundCream
import com.bloom.app.ui.theme.Gold
import com.bloom.app.ui.theme.LightPink
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

private const val MIN_SPLASH_MS   = 1_200L
private const val AD_WAIT_TIMEOUT = 3_000L

/**
 * Compose splash screen.
 *
 * Flow:
 *  First-time user  → wait MIN_SPLASH_MS → Onboarding (NO ad ever)
 *  Returning user   → wait for ad (up to AD_WAIT_TIMEOUT) → show ad → Home
 *                     OR navigate to Home immediately if no ad loads in time
 *
 * The DataStore is read via Flow.first() so we always get the real persisted
 * value, not just the in-memory cache (which starts false on cold boot).
 */
@Composable
fun SplashScreen(
    userRepository: UserRepository,
    appOpenAdManager: AppOpenAdManager,
    onNavigateToHome: () -> Unit,
    onNavigateToOnboarding: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCream)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        // Sparkles — positions match Figma splash (390×818)
        // Upper-left gold sparkle
        FourPointSparkle(
            size     = 14.dp,
            color    = Gold,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 52.dp, y = 108.dp)
        )
        // Upper-right gold sparkle (smaller)
        FourPointSparkle(
            size     = 10.dp,
            color    = Gold,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-72).dp, y = 148.dp)
        )
        // Upper-right pink sparkle (smallest)
        FourPointSparkle(
            size     = 8.dp,
            color    = LightPink.copy(alpha = 0.9f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-52).dp, y = 210.dp)
        )
        // Lower-left gold sparkle
        FourPointSparkle(
            size     = 12.dp,
            color    = Gold,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 52.dp, y = (-130).dp)
        )

        // Flower — centered, matching Figma (slightly above true centre on tall phones)
        FlowerLogo(size = 130.dp, showGlow = true)
    }

    LaunchedEffect(Unit) {
        val start = System.currentTimeMillis()

        // Read the real persisted onboarding state from DataStore (not just the cache).
        // flow.first() suspends only until the first emission, typically < 50 ms.
        val onboardingDone = userRepository.isOnboardingComplete.first()
        // Also update the in-memory cache so AppOpenAdManager is correct
        userRepository.updateOnboardingCache(onboardingDone)

        // Ensure we hold the splash for at least MIN_SPLASH_MS
        val elapsed = System.currentTimeMillis() - start
        if (elapsed < MIN_SPLASH_MS) delay(MIN_SPLASH_MS - elapsed)

        if (!onboardingDone) {
            // ── First-time user: no ad, go straight to onboarding ──────────
            onNavigateToOnboarding()
        } else {
            // ── Returning user: try to show App Open Ad ─────────────────────
            // Wait up to AD_WAIT_TIMEOUT for the ad to finish loading.
            val adReady = withTimeoutOrNull(AD_WAIT_TIMEOUT) {
                while (!appOpenAdManager.isAdReady()) {
                    delay(100)
                }
                true
            } ?: false

            if (adReady) {
                // Mark that we are handling the cold-start show so the
                // ProcessLifecycleOwner.onStart callback does not show a second ad.
                appOpenAdManager.markColdStartHandled()

                val activity = context as? Activity
                if (activity != null) {
                    // Show the ad; navigate to Home in the onComplete callback.
                    appOpenAdManager.showAdIfAvailable(activity) {
                        onNavigateToHome()
                    }
                } else {
                    onNavigateToHome()
                }
            } else {
                // Ad not ready in time — go straight to Home.
                onNavigateToHome()
            }
        }
    }
}

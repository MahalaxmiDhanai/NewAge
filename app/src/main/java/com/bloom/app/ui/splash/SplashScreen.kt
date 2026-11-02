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
 * Splash screen with conditional App Open Ad flow.
 *
 * First-time users: wait 1.2s → onboarding (no ad)
 * Returning users: wait up to 3s for ad → show ad → home
 *
 * DataStore read via Flow.first() guarantees we get the actual persisted value,
 * not the in-memory cache (which defaults to false on cold start).
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

        // Read persisted onboarding state from DataStore.
        // first() suspends until first emission (~50ms), not just reading the cache.
        val onboardingDone = userRepository.isOnboardingComplete.first()
        // Update in-memory cache so AppOpenAdManager has correct state
        userRepository.updateOnboardingCache(onboardingDone)

        // Hold splash for minimum duration
        val elapsed = System.currentTimeMillis() - start
        if (elapsed < MIN_SPLASH_MS) delay(MIN_SPLASH_MS - elapsed)

        if (!onboardingDone) {
            // First-time user: no ad, straight to onboarding
            onNavigateToOnboarding()
        } else {
            // Returning user: try to show App Open Ad
            // Wait up to 3s for ad to load
            val adReady = withTimeoutOrNull(AD_WAIT_TIMEOUT) {
                while (!appOpenAdManager.isAdReady()) {
                    delay(100)
                }
                true
            } ?: false

            if (adReady) {
                // Mark cold-start handled to prevent ProcessLifecycleOwner
                // from showing a duplicate ad on its onStart callback.
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

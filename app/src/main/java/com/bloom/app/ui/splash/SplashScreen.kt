package com.bloom.app.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bloom.app.ads.AppOpenAdManager
import com.bloom.app.data.UserRepository
import com.bloom.app.ui.components.FlowerLogo
import com.bloom.app.ui.components.FourPointSparkle
import com.bloom.app.ui.theme.BackgroundCream
import com.bloom.app.ui.theme.Gold
import com.bloom.app.ui.theme.LightPink
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull

private const val MIN_SPLASH_MS   = 1_200L
private const val AD_WAIT_TIMEOUT = 3_000L

/**
 * Custom (Compose) splash screen drawn on top of the system splash.
 *
 * Flow:
 * 1. Read onboarding state from DataStore (via [userRepository]).
 * 2. If first-time user → wait min 1.2 s then navigate to Onboarding.
 * 3. If returning user → wait up to 3 s for an App Open Ad to be ready,
 *    show it (then Home), or navigate to Home directly if no ad loads in time.
 */
@Composable
fun SplashScreen(
    userRepository: UserRepository,
    appOpenAdManager: AppOpenAdManager,
    onNavigateToHome: () -> Unit,
    onNavigateToOnboarding: () -> Unit
) {
    Box(
        modifier        = Modifier
            .fillMaxSize()
            .background(BackgroundCream),
        contentAlignment = Alignment.Center
    ) {
        // Sparkles — scattered around the flower
        FourPointSparkle(
            size     = 14.dp,
            color    = Gold,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 50.dp, y = 120.dp)
        )
        FourPointSparkle(
            size     = 10.dp,
            color    = Gold,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-80).dp, y = 160.dp)
        )
        FourPointSparkle(
            size     = 10.dp,
            color    = LightPink,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-60).dp, y = 240.dp)
        )
        FourPointSparkle(
            size     = 12.dp,
            color    = Gold,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 48.dp, y = (-160).dp)
        )

        // Centered flower
        FlowerLogo(
            size     = 120.dp,
            showGlow = true
        )
    }

    // Navigation logic
    LaunchedEffect(Unit) {
        val start = System.currentTimeMillis()

        // Read onboarding state
        val onboardingDone = userRepository.onboardingCompletedCache.value

        // Wait at least MIN_SPLASH_MS for polish, minus time already spent
        val elapsed = System.currentTimeMillis() - start
        if (elapsed < MIN_SPLASH_MS) delay(MIN_SPLASH_MS - elapsed)

        if (!onboardingDone) {
            // First-time user — no ad, straight to onboarding
            onNavigateToOnboarding()
        } else {
            // Returning user — try to show an App Open Ad
            // Wait up to AD_WAIT_TIMEOUT for the ad to load
            val adShown = withTimeoutOrNull(AD_WAIT_TIMEOUT) {
                // Poll until ad is ready or timeout expires
                while (!appOpenAdManager.isAdReady()) {
                    delay(100)
                }
                true
            } ?: false

            if (adShown) {
                // Ad is ready — mark that we'll handle the cold-start show
                appOpenAdManager.markColdStartHandled()
                // We navigate to Home; showAdIfAvailable will be called
                // by the caller (MainActivity) right after navigation
                onNavigateToHome()
            } else {
                onNavigateToHome()
            }
        }
    }
}

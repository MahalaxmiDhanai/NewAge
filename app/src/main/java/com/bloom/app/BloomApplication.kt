package com.bloom.app

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.bloom.app.ads.AdConfig
import com.bloom.app.ads.AdAnalytics
import com.bloom.app.ads.AdsInitializer
import com.bloom.app.ads.AppOpenAdManager
import com.bloom.app.ads.LogcatAdAnalytics
import com.bloom.app.data.HomeRepository
import com.bloom.app.data.OnboardingPreferences
import com.bloom.app.data.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BloomApplication : Application() {

    // ── Manual DI container ────────────────────────────────────────────────
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    lateinit var onboardingPreferences: OnboardingPreferences
        private set
    lateinit var userRepository: UserRepository
        private set
    lateinit var homeRepository: HomeRepository
        private set
    lateinit var adsInitializer: AdsInitializer
        private set
    lateinit var appOpenAdManager: AppOpenAdManager
        private set
    val adAnalytics: AdAnalytics = LogcatAdAnalytics()

    override fun onCreate() {
        super.onCreate()

        // Build repositories
        onboardingPreferences = OnboardingPreferences(this)
        userRepository        = UserRepository(onboardingPreferences)
        homeRepository        = HomeRepository()

        // Bootstrap onboarding cache (non-blocking read for AppOpenAdManager eligibility)
        applicationScope.launch {
            val done = userRepository.isOnboardingComplete.first()
            userRepository.updateOnboardingCache(done)
            // Keep cache in sync as it changes
            userRepository.isOnboardingComplete.collect { value ->
                userRepository.updateOnboardingCache(value)
            }
        }

        // Ads
        adsInitializer   = AdsInitializer(this)
        appOpenAdManager = AppOpenAdManager(
            context        = this,
            userRepository = userRepository,
            analytics      = adAnalytics,
            adUnitId       = AdConfig.APP_OPEN_AD_UNIT_ID
        )

        // Register lifecycle observers
        ProcessLifecycleOwner.get().lifecycle.addObserver(appOpenAdManager)
        registerActivityLifecycleCallbacks(appOpenAdManager)

        // Initialize Mobile Ads, then preload App Open Ad
        adsInitializer.initialize(applicationScope) {
            appOpenAdManager.loadAd()
        }
    }
}

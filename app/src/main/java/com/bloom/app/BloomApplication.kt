package com.bloom.app

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
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

class BloomApplication : Application(), LifecycleEventObserver {

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
    
    private var lastCheckedDate: String? = null

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
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        registerActivityLifecycleCallbacks(appOpenAdManager)

        // Initialize Mobile Ads, then preload App Open Ad
        adsInitializer.initialize(applicationScope) {
            appOpenAdManager.loadAd()
        }
    }
    
    override fun onStateChanged(source: androidx.lifecycle.LifecycleOwner, event: androidx.lifecycle.Lifecycle.Event) {
        if (event == androidx.lifecycle.Lifecycle.Event.ON_START) {
            // App came to foreground - check if date changed
            checkAndResetIfNewDay()
        }
    }
    
    private fun checkAndResetIfNewDay() {
        val today = java.time.LocalDate.now().toString()
        if (lastCheckedDate != today) {
            lastCheckedDate = today
            applicationScope.launch {
                val savedDate = userRepository.lastProgressDate.first()
                if (savedDate != null && savedDate != today) {
                    // New day: reset progress
                    userRepository.resetDailyProgress(today)
                }
            }
        }
    }
}

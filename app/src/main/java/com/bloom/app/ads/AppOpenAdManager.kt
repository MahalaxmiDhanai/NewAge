package com.bloom.app.ads

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.bloom.app.data.UserRepository
import java.lang.ref.WeakReference

/**
 * App Open Ad manager with lifecycle-aware loading and display.
 *
 * Hooks into ProcessLifecycleOwner (foreground events) and ActivityLifecycleCallbacks
 * (track current Activity reference).
 *
 * Eligibility:
 * - Onboarding complete (checked via cached StateFlow, non-blocking)
 * - No ad currently showing
 * - Ad loaded and fresh (< 4 hours old)
 * - Cold-start already handled (coldStartHandled flag prevents duplicate)
 */
class AppOpenAdManager(
    private val context: Context,
    private val userRepository: UserRepository,
    private val analytics: AdAnalytics,
    private val adUnitId: String
) : DefaultLifecycleObserver, Application.ActivityLifecycleCallbacks {

    companion object {
        private const val TAG = "AppOpenAdManager"
        private const val AD_EXPIRY_MS = 4L * 60 * 60 * 1000L  // 4 hours
        private const val DEBOUNCE_MS = 1_000L
    }

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd  = false
    var isShowingAd = false
        private set

    private var loadTime = 0L

    // Tracks whether we already showed the ad on cold start so ProcessLifecycle doesn't double-fire
    private var coldStartHandled = false

    // Debounce guard: timestamp of last showAdIfAvailable call
    private var lastShowAttemptMs = 0L

    private var currentActivityRef: WeakReference<Activity>? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    // ── Ad loading ────────────────────────────────────────────────────────

    fun loadAd() {
        if (isLoadingAd) return
        if (appOpenAd != null && !isAdExpired()) return   // already have a fresh ad

        isLoadingAd = true
        analytics.log(LogcatAdAnalytics.APP_OPEN_REQUESTED)
        Log.d(TAG, "Loading App Open ad…")

        AppOpenAd.load(
            context,
            adUnitId,
            AdRequest.Builder().build(),
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    mainHandler.post {
                        appOpenAd  = ad
                        loadTime   = System.currentTimeMillis()
                        isLoadingAd = false
                        analytics.log(LogcatAdAnalytics.APP_OPEN_LOADED)
                        Log.d(TAG, "App Open ad loaded.")
                    }
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    mainHandler.post {
                        isLoadingAd = false
                        appOpenAd   = null
                        analytics.log(LogcatAdAnalytics.APP_OPEN_FAILED,
                            mapOf("code" to error.code, "message" to error.message))
                        Log.w(TAG, "App Open ad failed to load: ${error.code} — ${error.message}")
                        // No tight retry; will retry on next foreground event
                    }
                }
            }
        )
    }

    private fun isAdExpired(): Boolean =
        System.currentTimeMillis() - loadTime > AD_EXPIRY_MS

    // ── Ad showing ────────────────────────────────────────────────────────

    /**
     * Show ad if available and eligible, then invoke [onComplete].
     * If ad can't/shouldn't be shown, [onComplete] is called immediately.
     */
    fun showAdIfAvailable(activity: Activity, onComplete: () -> Unit) {
        if (isShowingAd) {
            Log.d(TAG, "Skip: already showing.")
            onComplete()
            return
        }
        if (!isEligible()) {
            Log.d(TAG, "Skip: not eligible.")
            onComplete()
            return
        }
        val ad = appOpenAd
        if (ad == null || isAdExpired()) {
            Log.d(TAG, "Skip: no valid ad.")
            appOpenAd = null
            loadAd()
            onComplete()
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
                analytics.log(LogcatAdAnalytics.APP_OPEN_SHOWN)
                Log.d(TAG, "App Open ad shown.")
            }

            override fun onAdDismissedFullScreenContent() {
                isShowingAd = false
                appOpenAd   = null
                analytics.log(LogcatAdAnalytics.APP_OPEN_DISMISSED)
                Log.d(TAG, "App Open ad dismissed.")
                loadAd()   // preload next
                onComplete()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                isShowingAd = false
                appOpenAd   = null
                analytics.log(LogcatAdAnalytics.APP_OPEN_FAILED,
                    mapOf("code" to error.code, "message" to error.message))
                Log.w(TAG, "App Open ad failed to show: ${error.code}")
                loadAd()
                onComplete()
            }
        }

        ad.show(activity)
    }

    private fun isEligible(): Boolean {
        // Never show until onboarding is complete (non-blocking cache)
        return userRepository.onboardingCompletedCache.value
    }

    // ── Cold-start helpers ────────────────────────────────────────────────

    /** Mark cold-start handled (called from SplashScreen after 3s timeout). */
    fun markColdStartHandled() {
        coldStartHandled = true
    }

    fun clearColdStartFlag() {
        coldStartHandled = false
    }

    /** Check if ad is ready to show immediately (no waiting required). */
    fun isAdReady(): Boolean = appOpenAd != null && !isAdExpired() && !isLoadingAd

    // ── DefaultLifecycleObserver — ProcessLifecycleOwner ─────────────────

    override fun onStart(owner: LifecycleOwner) {
        if (coldStartHandled) {
            // Skip the first onStart that fires right after our cold-start show
            coldStartHandled = false
            Log.d(TAG, "onStart: skipping (cold-start already handled).")
            return
        }

        val now = System.currentTimeMillis()
        if (now - lastShowAttemptMs < DEBOUNCE_MS) {
            Log.d(TAG, "onStart: debounced.")
            return
        }
        lastShowAttemptMs = now

        val activity = currentActivityRef?.get() ?: return
        if (!isEligible()) return

        Log.d(TAG, "onStart: showing App Open ad for foreground event.")
        showAdIfAvailable(activity) { /* no-op — caller doesn't need a callback here */ }
    }

    // ── ActivityLifecycleCallbacks ────────────────────────────────────────

    override fun onActivityResumed(activity: Activity) {
        currentActivityRef = WeakReference(activity)
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivityRef?.get() === activity) {
            currentActivityRef = null
        }
    }

    // Unused callbacks — required by interface
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
}

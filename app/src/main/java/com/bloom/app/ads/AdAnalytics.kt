package com.bloom.app.ads

import android.util.Log

/**
 * Analytics interface — swap the implementation for Firebase or any other
 * analytics provider without touching call sites.
 */
interface AdAnalytics {
    fun log(event: String, params: Map<String, Any> = emptyMap())
}

class LogcatAdAnalytics : AdAnalytics {
    override fun log(event: String, params: Map<String, Any>) {
        val paramStr = if (params.isEmpty()) "" else " | $params"
        Log.d(TAG, "$event$paramStr")
    }

    companion object {
        private const val TAG = "BloomAds"

        // Named event constants
        const val APP_OPEN_REQUESTED  = "app_open_ad_requested"
        const val APP_OPEN_LOADED     = "app_open_ad_loaded"
        const val APP_OPEN_FAILED     = "app_open_ad_failed"
        const val APP_OPEN_SHOWN      = "app_open_ad_shown"
        const val APP_OPEN_DISMISSED  = "app_open_ad_dismissed"
        const val BANNER_LOADED       = "banner_ad_loaded"
        const val BANNER_FAILED       = "banner_ad_failed"
        const val BANNER_IMPRESSION   = "banner_ad_impression"
    }
}

package com.bloom.app.ads

import android.view.ViewGroup
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

private enum class BannerAdState { Idle, Loaded, Failed }

/**
 * Adaptive banner ad composable.
 *
 * - Width is computed from the device screen width (no hardcoded value).
 * - Height is reserved only after the ad loads to avoid content jumping.
 * - Slot collapses to zero height on failure — no empty gap.
 * - Pauses/resumes/destroys the AdView with the host lifecycle.
 */
@Composable
fun BannerAdView(
    adUnitId: String,
    analytics: AdAnalytics,
    modifier: Modifier = Modifier
) {
    val context       = LocalContext.current
    val lifecycle     = LocalLifecycleOwner.current.lifecycle
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val unitId        = adUnitId  // capture to avoid shadowing inside lambda

    var adState by remember { mutableStateOf(BannerAdState.Idle) }

    // Re-create AdView when orientation changes (screenWidthDp changes)
    val adView = remember(screenWidthDp) {
        AdView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setAdSize(
                AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                    context, screenWidthDp
                )
            )
            this.adUnitId = unitId
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    adState = BannerAdState.Loaded
                    analytics.log(LogcatAdAnalytics.BANNER_LOADED)
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    adState = BannerAdState.Failed
                    analytics.log(LogcatAdAnalytics.BANNER_FAILED,
                        mapOf("code" to error.code))
                }
                override fun onAdImpression() {
                    analytics.log(LogcatAdAnalytics.BANNER_IMPRESSION)
                }
            }
            loadAd(AdRequest.Builder().build())
        }
    }

    // Lifecycle: pause / resume / destroy
    DisposableEffect(adView, lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME  -> adView.resume()
                Lifecycle.Event.ON_PAUSE   -> adView.pause()
                Lifecycle.Event.ON_DESTROY -> adView.destroy()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            adView.destroy()
        }
    }

    // Only show the slot when the ad has loaded — collapses to nothing on failure
    if (adState == BannerAdState.Loaded) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .animateContentSize(),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { adView },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

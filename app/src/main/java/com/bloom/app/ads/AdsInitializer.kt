package com.bloom.app.ads

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdsInitializer(private val context: Context) {

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized

    fun initialize(scope: CoroutineScope, onReady: () -> Unit = {}) {
        scope.launch(Dispatchers.IO) {
            MobileAds.initialize(context) { status ->
                Log.d("BloomAds", "MobileAds initialized: ${status.adapterStatusMap}")
                _isInitialized.value = true
                onReady()
            }
        }
    }
}

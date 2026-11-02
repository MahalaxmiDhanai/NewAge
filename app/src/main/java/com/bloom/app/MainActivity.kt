package com.bloom.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.bloom.app.navigation.NavGraph
import com.bloom.app.ui.theme.BloomTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var keepSplashOnScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install system splash and keep it until onboarding state is loaded
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }

        super.onCreate(savedInstanceState)

        // Load onboarding state asynchronously
        val app = application as BloomApplication
        lifecycleScope.launch {
            app.userRepository.isOnboardingComplete.first()
            // DataStore loaded, allow splash to dismiss and hand off to Compose splash
            keepSplashOnScreen = false
        }

        enableEdgeToEdge()

        setContent {
            BloomTheme {
                val navController = rememberNavController()
                
                // Handle system back button
                BackHandler {
                    if (!navController.popBackStack()) {
                        finish()
                    }
                }
                
                NavGraph(
                    navController    = navController,
                    userRepository   = app.userRepository,
                    homeRepository   = app.homeRepository,
                    appOpenAdManager = app.appOpenAdManager
                )
            }
        }
    }
}

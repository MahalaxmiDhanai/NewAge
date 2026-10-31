package com.bloom.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.bloom.app.navigation.NavGraph
import com.bloom.app.ui.theme.BloomTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install the system splash screen (API 12+ cream background)
        installSplashScreen()

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val app = application as BloomApplication

        setContent {
            BloomTheme {
                val navController = rememberNavController()
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

package com.bloom.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bloom.app.ads.AppOpenAdManager
import com.bloom.app.data.HomeRepository
import com.bloom.app.data.UserRepository
import com.bloom.app.ui.home.HomeScreen
import com.bloom.app.ui.onboarding.OnboardingScreen
import com.bloom.app.ui.splash.SplashScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    userRepository: UserRepository,
    homeRepository: HomeRepository,
    appOpenAdManager: AppOpenAdManager
) {
    NavHost(
        navController  = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                userRepository    = userRepository,
                appOpenAdManager  = appOpenAdManager,
                onNavigateToHome  = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToOnboarding = {
                    navController.navigate(Routes.ONBOARDING) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                userRepository = userRepository,
                onFinished = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                homeRepository = homeRepository,
                userRepository = userRepository
            )
        }
    }
}

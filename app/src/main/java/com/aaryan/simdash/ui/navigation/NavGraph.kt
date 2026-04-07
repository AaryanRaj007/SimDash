package com.aaryan.simdash.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aaryan.simdash.ui.screens.*

@Composable
fun MainNavGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(NavRoutes.Onboarding.route) {
            OnboardingScreen(onFinish = {
                navController.navigate(NavRoutes.Home.route) {
                    popUpTo(NavRoutes.Onboarding.route) { inclusive = true }
                }
            })
        }
        composable(NavRoutes.Home.route) {
            HomeScreen()
        }
        composable(NavRoutes.AlertSettings.route) {
            AlertSettingsScreen()
        }
        composable(NavRoutes.UsageHistory.route) {
            UsageHistoryScreen()
        }
        composable(NavRoutes.SimInfo.route) {
            SimInfoScreen()
        }
        composable(NavRoutes.Settings.route) {
            SettingsScreen()
        }
    }
}

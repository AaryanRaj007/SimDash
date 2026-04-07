package com.aaryan.simdash.ui.navigation

sealed class NavRoutes(val route: String) {
    object Onboarding : NavRoutes("onboarding")
    object Home : NavRoutes("home")
    object AlertSettings : NavRoutes("alert_settings")
    object UsageHistory : NavRoutes("usage_history")
    object SimInfo : NavRoutes("sim_info")
    object Settings : NavRoutes("settings")
}

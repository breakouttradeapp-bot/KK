package com.aikundli.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aikundli.ui.screens.*
import com.aikundli.viewmodel.KundliViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object GenerateKundli : Screen("generate_kundli")
    object KundliResult : Screen("kundli_result")
    object DailyHoroscope : Screen("daily_horoscope")
    object SavedReports : Screen("saved_reports")
    object Settings : Screen("settings")
    object PrivacyPolicy : Screen("privacy_policy")
    object Terms : Screen("terms")
    object Premium : Screen("premium")
}

@Composable
fun KundliNavGraph(
    navController: NavHostController = rememberNavController()
) {

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.GenerateKundli.route) {

            val sharedVm: KundliViewModel =
                viewModel(navController.getBackStackEntry(Screen.Home.route))

            GenerateKundliScreen(
                navController = navController,
                viewModel = sharedVm
            )
        }

        composable(Screen.KundliResult.route) {

            val sharedVm: KundliViewModel =
                viewModel(navController.getBackStackEntry(Screen.Home.route))

            KundliResultScreen(
                navController = navController,
                viewModel = sharedVm
            )
        }

        composable(Screen.DailyHoroscope.route) {
            DailyHoroscopeScreen(navController = navController)
        }

        composable(Screen.SavedReports.route) {
            SavedReportsScreen(navController = navController)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }

        composable(Screen.PrivacyPolicy.route) {
            PrivacyPolicyScreen(navController = navController)
        }

        composable(Screen.Terms.route) {
            TermsScreen(navController = navController)
        }

        composable(Screen.Premium.route) {
            PremiumScreen(navController = navController)
        }
    }
}

package com.grusie.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.grusie.presentation.ui.admin.AdminDetailScreen
import com.grusie.presentation.ui.admin.AdminScreen
import com.grusie.presentation.ui.auth.LoginScreen
import com.grusie.presentation.ui.main.MainScreen
import com.grusie.presentation.ui.setting.SettingScreen
import com.grusie.presentation.ui.splash.SplashScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) { SplashScreen(navController) }
        composable(Routes.LOGIN) { LoginScreen(navController) }
        composable(Routes.MAIN) { MainScreen(navController) }
        composable(Routes.SETTING) { SettingScreen(navController) }
        composable(Routes.ADMIN) { AdminScreen(navController) }
        composable(
            "${Routes.DETAIL_ADMIN}?type={type}",
            arguments = listOf(
                navArgument(Routes.AdminKeys.type) { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val adminType = backStackEntry.arguments?.getString(Routes.AdminKeys.type) ?: ""

            AdminDetailScreen(navController, adminType)
        }
    }
}
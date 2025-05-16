package com.grusie.presentation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.grusie.presentation.ui.admin.AdminDetailModify
import com.grusie.presentation.ui.admin.AdminDetailScreen
import com.grusie.presentation.ui.admin.AdminScreen
import com.grusie.presentation.ui.auth.LoginScreen
import com.grusie.presentation.ui.main.MainScreen
import com.grusie.presentation.ui.setting.SettingScreen
import com.grusie.presentation.ui.splash.SplashScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    val adminTypeArgs = Routes.AdminKeys.EXTRA_ADMIN_TYPE
    val dataArgs = Routes.Keys.EXTRA_DATA

    NavHost(
        navController,
        startDestination = Routes.SPLASH,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }) {
        composable(Routes.SPLASH) { SplashScreen(navController) }
        composable(Routes.LOGIN) { LoginScreen(navController) }
        composable(Routes.MAIN) { MainScreen(navController) }
        composable(Routes.SETTING) { SettingScreen(navController) }
        composable(Routes.ADMIN) { AdminScreen(navController) }
        composable(
            "${Routes.DETAIL_ADMIN}?${adminTypeArgs}={${adminTypeArgs}}",
            arguments = listOf(
                navArgument(adminTypeArgs) { type = NavType.StringType },
            )
        ) {
            AdminDetailScreen(navController)
        }
        composable(
            "${Routes.DETAIL_ADMIN_MODIFY}?$dataArgs={$dataArgs}",
            arguments = listOf(
                navArgument(dataArgs) { type = NavType.StringType }
            )
        ) {
            AdminDetailModify(navController)
        }
    }
}
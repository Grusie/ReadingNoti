package com.grusie.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
    }
}
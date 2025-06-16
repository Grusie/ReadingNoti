package com.grusie.presentation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.grusie.presentation.ui.admin.AdminDetailModify
import com.grusie.presentation.ui.admin.AdminDetailScreen
import com.grusie.presentation.ui.admin.AdminScreen
import com.grusie.presentation.ui.auth.LoginScreen
import com.grusie.presentation.ui.auth.SignUpScreen
import com.grusie.presentation.ui.main.MainScreen
import com.grusie.presentation.ui.msg.MsgAppListScreen
import com.grusie.presentation.ui.msg.MsgListScreen
import com.grusie.presentation.ui.permission.PermissionRequestScreen
import com.grusie.presentation.ui.setting.SettingScreen
import com.grusie.presentation.ui.splash.SplashScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    val adminTypeArgs = Routes.AdminKeys.EXTRA_ADMIN_TYPE
    val dataArgs = Routes.Keys.EXTRA_DATA
    val extraAuth = Routes.PermissionKeys.EXTRA_AUTH
    val extraAppId = Routes.MsgKeys.EXTRA_APP_ID
    val extraAppDisplayName = Routes.MsgKeys.EXTRA_APP_NAME

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

        composable(
            Routes.SIGNUP
        ) { SignUpScreen(navController) }

        composable(
            "${Routes.PERMISSION}?${extraAuth}={$extraAuth}",
            arguments = listOf(
                navArgument(extraAuth) { type = NavType.BoolType}
            )
        ) {backStackEntry ->
            val isAuth = backStackEntry.arguments?.getBoolean(extraAuth) ?: false
            val onPermissionGranted = remember(isAuth) {
                {
                    val navigation = if (isAuth) Routes.MAIN else Routes.LOGIN
                    navController.navigate(navigation) {
                        popUpTo("${Routes.PERMISSION}?${extraAuth}={$extraAuth}") { inclusive = true }
                    }
                }
            }

            PermissionRequestScreen(
                onPermissionGranted = onPermissionGranted
            )
        }

        composable(Routes.MSG_APP_LIST) { MsgAppListScreen(navController) }
        composable(
            "${Routes.MSG_LIST}?${extraAppId}={$extraAppId}&${extraAppDisplayName}={$extraAppDisplayName}",
            arguments = listOf(
                navArgument(extraAppId) { type = NavType.IntType},
                navArgument(extraAppDisplayName) { type = NavType.StringType}
            )
        ) {
            MsgListScreen(navController)
        }
    }
}
package com.grusie.presentation.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.grusie.presentation.R
import com.grusie.presentation.Routes
import com.grusie.presentation.ui.base.BaseEventState
import com.grusie.presentation.ui.base.BaseUiState
import com.grusie.presentation.ui.common.CircleProgressBar
import com.grusie.presentation.ui.common.OneButtonAlertDialog
import kotlin.system.exitProcess

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {

    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current
    var isShowErrorDialog by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    var isFinishApp by remember { mutableStateOf(false) }   // 에러 시 앱을 종료 할 건지에 대한 플래그

    LaunchedEffect(Unit) {
        viewModel.eventState.collect { eventState ->
            if (eventState != null) {
                when (eventState) {
                    is BaseEventState.Navigate -> {
                        navController.navigate(eventState.route) {
                            if (eventState.includeBackStack) {
                                popUpTo(Routes.SPLASH) { inclusive = true }
                            }
                        }
                    }

                    is SplashEventState.FinishableError -> {
                        errorMsg = eventState.errorMsg
                        isFinishApp = eventState.isFinishApp
                        isShowErrorDialog = true
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Splash Screen")
            }

            when (uiState) {
                is BaseUiState.Loading -> {
                    CircleProgressBar()
                }
            }

            OneButtonAlertDialog(
                isShowDialog = isShowErrorDialog,
                onClickConfirm = {
                    isShowErrorDialog = false
                    if (isFinishApp) {
                        exitProcess(0)
                    }
                },
                title = context.getString(R.string.common_error_title_notice_msg),
                content = errorMsg,
            )
        }
    }
}
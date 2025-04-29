package com.grusie.presentation.ui.auth

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.grusie.core.utils.Logger
import com.grusie.presentation.BuildConfig
import com.grusie.presentation.R
import com.grusie.presentation.Routes
import com.grusie.presentation.ui.common.CircleProgressBar
import com.grusie.presentation.ui.common.OneButtonAlertDialog
import com.grusie.presentation.ui.common.TwoButtonAlertDialog
import com.grusie.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authUiState by viewModel.uiState.collectAsState()
    var isShowErrorDialog by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    var alertTitle by remember { mutableStateOf("") }
    var alertMsg by remember { mutableStateOf("") }
    var isShowAlertDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.eventState.collect { eventState ->
            if (eventState != null)
                when (eventState) {
                    is LoginEventState.Navigate -> {
                        try {
                            navController.navigate(eventState.route) {
                                if (eventState.includeBackStack) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                }
                            }
                        } catch (e: Exception) {
                            Logger.e("LoginScreen Navigate Error", e)
                            isShowErrorDialog = true
                        }
                    }

                    is LoginEventState.Error -> {
                        errorMsg = eventState.errorMsg
                        isShowErrorDialog = true
                    }

                    is LoginEventState.Alert -> {
                        alertTitle = eventState.title
                        alertMsg = eventState.msg
                        isShowAlertDialog = true
                    }
                }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = {
            scope.launch {
                handleSignIn(context) { viewModel.requestGoogleSignIn(it) }
            }
        }) {
            Text("Login")
        }

        when (authUiState) {
            is LoginUiState.Loading -> {
                CircleProgressBar()
            }
        }

        OneButtonAlertDialog(
            isShowDialog = isShowErrorDialog,
            onClickConfirm = { isShowErrorDialog = false },
            title = context.getString(R.string.common_error_title_notice_msg),
            content = errorMsg,
        )

        TwoButtonAlertDialog(
            isShowDialog = isShowAlertDialog,
            onClickConfirm = {
                isShowErrorDialog = false
                viewModel.coverPersonalSetting(true)
            },
            onClickCancel = {
                isShowErrorDialog = false
                viewModel.coverPersonalSetting(false)
            },
            onDismiss = {},
            title = alertTitle,
            content = alertMsg,
        )


    }
}


/**
 * 구글 로그인을 위한 계정을 선택하도록 화면에 띄우는 함수
 */
private suspend fun handleSignIn(context: Context, requestGoogleSignIn: (String) -> Unit) {
    val credentialManager = CredentialManager.create(context)
    val googleIdOption = GetGoogleIdOption.Builder()
        .setServerClientId(BuildConfig.GOOGLE_AUTH_CLIENT_ID)
        .setFilterByAuthorizedAccounts(false)
        .build()
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val googleSignInRequest = credentialManager.getCredential(
            request = request,
            context = context
        )
        val credential = googleSignInRequest.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            requestGoogleSignIn(googleIdTokenCredential.idToken)
        }
    } catch (e: Exception) {
        Logger.e("LoginScreen handleSignIn Error", e)
    }
}

package com.grusie.presentation.ui.auth

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.grusie.presentation.ui.base.BaseEventState
import com.grusie.presentation.ui.base.BaseUiState
import com.grusie.presentation.ui.common.CircleProgressBar
import com.grusie.presentation.ui.common.CommonTextField
import com.grusie.presentation.ui.common.OneButtonAlertDialog
import com.grusie.presentation.ui.common.TwoButtonAlertDialog
import com.grusie.presentation.ui.common.debounceClickable
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
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.eventState.collect { eventState ->
            if (eventState != null)
                when (eventState) {
                    is BaseEventState.Navigate -> {
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

                    is BaseEventState.Error -> {
                        errorMsg = eventState.errorMsg
                        isShowErrorDialog = true
                    }

                    is BaseEventState.Alert -> {
                        alertTitle = eventState.title
                        alertMsg = eventState.msg
                        isShowAlertDialog = true
                    }
                }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                })
            },
        contentAlignment = Alignment.Center
    ) {
        val isPasswordVisible = viewModel.isPasswordVisible.collectAsState().value
        val idText = viewModel.idText.collectAsState().value
        val pwText = viewModel.pwText.collectAsState().value

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .imePadding(),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                modifier = Modifier.padding(bottom = 20.dp),
                text = "환영합니다.",
                fontWeight = FontWeight.SemiBold,
                fontSize = 28.sp
            )

            CommonTextField(
                value = idText,
                hint = context.getString(R.string.str_id_hint),
                onValueChanged = { viewModel.setIdText(it) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                isTrailingVisible = idText.isNotEmpty(),
                trailIcon = { Icon(Icons.Default.Clear, "") },
                trailButtonClick = { viewModel.setIdText("") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            CommonTextField(
                value = pwText,
                hint = context.getString(R.string.str_password_hint),
                onValueChanged = { viewModel.setPwText(it) },
                isTrailingVisible = pwText.isNotEmpty(),
                isPasswordStyle = !isPasswordVisible,
                trailIcon = {
                    Icon(
                        if (isPasswordVisible) painterResource(R.drawable.ic_visible)
                        else painterResource(R.drawable.ic_invisible),
                        contentDescription = "ic_password_visible"
                    )
                },
                trailButtonClick = {
                    viewModel.changePasswordVisible()
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                    .padding(vertical = 16.dp),
                text = context.getString(R.string.str_login),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )

                // 가운데 텍스트
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "or",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    fontSize = 16.sp
                )

                // 오른쪽 Divider
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )
            }

            GoogleSignInButton(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
                focusManager.clearFocus()
                keyboardController?.hide()
                scope.launch {
                    handleSignIn(context) { viewModel.requestGoogleSignIn(it) }
                }
            })

            Spacer(modifier = Modifier.height(40.dp))
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        viewModel.setEventState(BaseEventState.Navigate(""))
                    },
                text = context.getString(R.string.str_sign_up),
                color = MaterialTheme.colorScheme.primary
            )
        }

        when (authUiState) {
            is BaseUiState.Loading -> {
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
                isShowAlertDialog = false
                viewModel.coverPersonalSetting(true)
            },
            onClickCancel = {
                isShowAlertDialog = false
                viewModel.coverPersonalSetting(false)
            },
            onDismiss = {},
            title = alertTitle,
            content = alertMsg,
        )
    }
}

@Composable
fun GoogleSignInButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val image = if (isDarkTheme) {
        painterResource(R.drawable.android_dark_rd_na)
    } else {
        painterResource(R.drawable.android_light_rd_na)
    }

    Image(
        painter = image,
        contentDescription = "Sign in with Google",
        modifier = modifier
            .clip(shape = CircleShape)
            .debounceClickable { onClick() }
    )
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


@Composable
@Preview(showBackground = true)
fun GoogleSignInButtonPreview() {
    GoogleSignInButton {}
}
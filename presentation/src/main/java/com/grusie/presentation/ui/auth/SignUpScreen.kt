package com.grusie.presentation.ui.auth

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.grusie.core.utils.LoggerProvider
import com.grusie.presentation.R
import com.grusie.presentation.Routes
import com.grusie.presentation.ui.base.BaseEventState
import com.grusie.presentation.ui.base.BaseUiState
import com.grusie.presentation.ui.common.CircleProgressBar
import com.grusie.presentation.ui.common.CommonTextField
import com.grusie.presentation.ui.common.CommonTitleBar
import com.grusie.presentation.ui.common.OneButtonAlertDialog
import com.grusie.presentation.ui.common.debounceClickable
import com.grusie.presentation.viewmodel.AuthViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SignUpScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context: Context = LocalContext.current
    val authUiState by viewModel.uiState.collectAsState()
    var isShowErrorDialog by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    var alertTitle by remember { mutableStateOf("") }
    var alertMsg by remember { mutableStateOf("") }
    var isShowAlertDialog by remember { mutableStateOf(false) }
    var onAlertConfirm: () -> Unit = {}
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
                            LoggerProvider.logger.e("LoginScreen Navigate Error", e)
                            isShowErrorDialog = true
                        }
                    }

                    is BaseEventState.PopBackStack -> {
                        navController.popBackStack()
                    }

                    is BaseEventState.Error -> {
                        errorMsg = eventState.errorMsg
                        isShowErrorDialog = true
                    }

                    is BaseEventState.Alert -> {
                        alertTitle = eventState.title
                        alertMsg = eventState.msg
                        onAlertConfirm = eventState.onConfirm
                        isShowAlertDialog = true
                    }
                }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { CommonTitleBar(title = context.getString(R.string.str_sign_up), navController = navController) }
    ) { paddingValues ->
        Box(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                })
            }
        ) {
            val scrollState = rememberScrollState()
            val isPasswordVisible = viewModel.isPasswordVisible.collectAsState().value
            val isPasswordConfirmVisible = viewModel.isPasswordConfirmVisible.collectAsState().value
            val nickNameText = viewModel.nickNameText.collectAsState().value
            val pwConfirmText = viewModel.pwConfirmText.collectAsState().value
            val idText = viewModel.idText.collectAsState().value
            val pwText = viewModel.pwText.collectAsState().value

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(scrollState)
                    .imePadding()
            ) {
                Spacer(modifier = Modifier.height(LocalConfiguration.current.screenHeightDp.dp * 0.1f))

                Text(
                    modifier = Modifier.padding(bottom = 20.dp),
                    text = context.getString(R.string.str_sign_up),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 28.sp
                )

                CommonTextField(
                    value = nickNameText,
                    hint = context.getString(R.string.str_nickname),
                    onValueChanged = { viewModel.setNickNameText(it) },
                    isTrailingVisible = nickNameText.isNotEmpty(),
                    trailIcon = { Icon(Icons.Default.Clear, "") },
                    trailButtonClick = { viewModel.setNickNameText("") },
                )

                Spacer(modifier = Modifier.height(20.dp))
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

                Spacer(modifier = Modifier.height(20.dp))
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
                CommonTextField(
                    value = pwConfirmText,
                    hint = context.getString(R.string.str_password_confirm_hint),
                    onValueChanged = { viewModel.setPwConfirmText(it) },
                    isTrailingVisible = pwConfirmText.isNotEmpty(),
                    isPasswordStyle = !isPasswordConfirmVisible,
                    trailIcon = {
                        Icon(
                            if (isPasswordConfirmVisible) painterResource(R.drawable.ic_visible)
                            else painterResource(R.drawable.ic_invisible),
                            contentDescription = "ic_password_visible"
                        )
                    },
                    trailButtonClick = {
                        viewModel.changePasswordConfirmVisible()
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
                        .debounceClickable {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            viewModel.emailSignUp()
                        }
                        .padding(vertical = 16.dp),
                    text = context.getString(R.string.str_sign_up),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
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

            OneButtonAlertDialog(
                isShowDialog = isShowAlertDialog,
                onClickConfirm = {
                    isShowAlertDialog = false
                    onAlertConfirm()
                },
                onDismiss = {},
                title = alertTitle,
                content = alertMsg,
            )
        }
    }
}
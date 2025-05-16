package com.grusie.presentation.ui.admin

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.grusie.core.utils.Logger
import com.grusie.presentation.R
import com.grusie.presentation.Routes
import com.grusie.presentation.data.setting.AdminSettingEnum
import com.grusie.presentation.ui.common.CircleProgressBar
import com.grusie.presentation.ui.common.CommonTitleBar
import com.grusie.presentation.ui.common.OneButtonAlertDialog

@Composable
fun AdminScreen(
    navController: NavHostController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                viewModel.uploadImageToStorage("test", uri)
            } else {
                Logger.e("AdminScreen", "imagePicker Error : imageUrl is Null")
            }
        }

    val uiState = viewModel.uiState.collectAsState().value
    var errorMsg by remember { mutableStateOf("") }
    var isShowErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.eventState.collect { eventState ->
            if (eventState != null) {
                when (eventState) {
                    is AdminEventState.Error -> {
                        errorMsg = eventState.errorMsg
                        isShowErrorDialog = true
                    }

                    is AdminEventState.Toast -> {
                        Toast.makeText(context, eventState.toastMsg, Toast.LENGTH_SHORT).show()
                    }

                    is AdminEventState.Navigate -> {
                        val fullRoute = buildString {
                            append(eventState.route)
                            if (eventState.args.isNotEmpty()) {
                                append("?")
                                append(eventState.args.entries.joinToString("&") { "${it.key}=${it.value}" })
                            }
                        }
                        navController.navigate(fullRoute) {
                            if (eventState.includeBackStack) {
                                popUpTo(Routes.ADMIN) { inclusive = true }
                            }
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CommonTitleBar(
                title = context.getString(R.string.title_admin),
                navController = navController
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                AdminSettingEnum.entries.forEach {
                    AdminSettingListItem(context, viewModel, type = it)

                    if (AdminSettingEnum.entries.last() != it) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                            thickness = 1.dp,
                        )
                    }
                }
            }
            Button(onClick = {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }) {
                Text(text = "이미지 선택 후 업로드")
            }

            when (uiState) {
                AdminUiState.Loading -> {
                    CircleProgressBar()
                }
            }

            OneButtonAlertDialog(
                isShowDialog = isShowErrorDialog,
                onClickConfirm = { isShowErrorDialog = false },
                title = context.getString(R.string.common_error_title_notice_msg),
                content = errorMsg,
            )
        }

    }
}

@Composable
fun AdminSettingListItem(
    context: Context,
    viewModel: AdminViewModel? = null,
    type: AdminSettingEnum
) {
    Column(
        modifier = Modifier
            .defaultMinSize(minHeight = 70.dp)
            .fillMaxWidth()
            .clickable {
                viewModel?.run {
                    setEventState(
                        AdminEventState.Navigate(
                            Routes.DETAIL_ADMIN,
                            args = mapOf(Routes.AdminKeys.EXTRA_ADMIN_TYPE to type.name)
                        )
                    )
                }
            }
            .padding(vertical = 8.dp, horizontal = 20.dp)

    ) {
        Text(
            type.getTitle(context),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            type.getContent(context),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            fontSize = 14.sp
        )
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xffffffff)
fun AdminSettingListItemPreview() {
    AdminSettingListItem(context = LocalContext.current, type = AdminSettingEnum.MANAGE_ADMIN)
}
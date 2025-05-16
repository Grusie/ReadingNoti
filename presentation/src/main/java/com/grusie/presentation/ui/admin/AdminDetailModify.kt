package com.grusie.presentation.ui.admin

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.grusie.core.common.SettingFieldEnum
import com.grusie.core.common.SettingType
import com.grusie.presentation.R
import com.grusie.presentation.Routes
import com.grusie.presentation.data.setting.AdminSettingEnum
import com.grusie.presentation.data.setting.totalmenu.UiTotalSettingDto
import com.grusie.presentation.ui.common.CircleProgressBar
import com.grusie.presentation.ui.common.CommonSwitch
import com.grusie.presentation.ui.common.CommonTextField
import com.grusie.presentation.ui.common.CommonTitleBar
import com.grusie.presentation.ui.common.OneButtonAlertDialog
import com.grusie.presentation.ui.common.TitleButtonItem
import com.grusie.presentation.ui.common.TwoButtonAlertDialog

@Composable
fun AdminDetailModify(
    navController: NavHostController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val detailTotalSettingDto = viewModel.detailTotalSettingDto.collectAsState().value

    val context = LocalContext.current
    val uiState = viewModel.uiState.collectAsState().value
    var errorMsg by remember { mutableStateOf("") }
    var isShowErrorDialog by remember { mutableStateOf(false) }
    var isShowConfirmDialog by remember { mutableStateOf(false) }
    var isConfirmType by remember { mutableIntStateOf(0) }

    // 뒤로가기 버튼 눌렀을 때 저장 안 하냐는 Alert를 띄움
    BackHandler {
        viewModel.setEventState(AdminEventState.Confirm(AdminViewModel.ConfirmType.CANCEL))
    }

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

                    is AdminEventState.Confirm -> {
                        isShowConfirmDialog = true
                        isConfirmType = eventState.type
                    }

                    is AdminEventState.Success -> {
                        when (eventState.data) {
                            AdminViewModel.SuccessType.SUCCESS_MODIFY -> navController.popBackStack()
                        }
                    }
                }
            }
        }
    }

    when (viewModel.adminTypeEnum) {
        AdminSettingEnum.MANAGE_TOTAL_MENU -> {

        }

        else -> {}
    }

    Scaffold(topBar = {
        CommonTitleBar(
            title = viewModel.initDetailTotalSettingDto?.displayName ?: "",
            leftButton = listOf(
                TitleButtonItem(
                    iconRes = null,
                    {
                        viewModel.setEventState(AdminEventState.Confirm(AdminViewModel.ConfirmType.CANCEL))
                    },
                    text = context.getString(R.string.str_cancel)
                )
            ),
            rightButton = listOf(TitleButtonItem(iconRes = null, {
                viewModel.setEventState(AdminEventState.Confirm(AdminViewModel.ConfirmType.CONFIRM))
            }, text = context.getString(R.string.str_confirm), MaterialTheme.colorScheme.primary)),
            navController = navController,
            isTitleCenter = true
        )
    }) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {

            detailTotalSettingDto?.let {

                LazyColumn {
                    items(if (it.type == SettingType.GENERAL) SettingFieldEnum.getGeneralField() else SettingFieldEnum.getAppField()) { item ->
                        when (item.type) {
                            SettingFieldEnum.FieldType.BOOLEAN_TYPE -> {
                                val (initChecked, isChecked) = when (item) {
                                    SettingFieldEnum.VISIBLE -> Pair(
                                        viewModel.initDetailTotalSettingDto?.isVisible ?: false,
                                        detailTotalSettingDto.isVisible
                                    )

                                    SettingFieldEnum.ENABLED -> Pair(
                                        viewModel.initDetailTotalSettingDto?.isInitEnabled ?: false,
                                        detailTotalSettingDto.isInitEnabled
                                    )

                                    else -> Pair(false, false)
                                }

                                ModifyListBooleanItem(
                                    initChecked,
                                    item,
                                    isChecked,
                                    onValueChanged = { isSelected ->
                                        onValueChanged(
                                            viewModel,
                                            detailTotalSettingDto,
                                            item,
                                            content = isSelected
                                        )
                                    }
                                )
                            }

                            SettingFieldEnum.FieldType.STRING_TYPE -> {
                                val (initContent, content) = when (item) {
                                    SettingFieldEnum.DISPLAY_NAME -> Pair(
                                        viewModel.initDetailTotalSettingDto!!.displayName,
                                        detailTotalSettingDto.displayName
                                    )

                                    SettingFieldEnum.DESCRIPTION -> Pair(
                                        viewModel.initDetailTotalSettingDto!!.description,
                                        detailTotalSettingDto.description
                                    )

                                    SettingFieldEnum.PACKAGE -> Pair(
                                        viewModel.initDetailTotalSettingDto!!.packageName ?: "",
                                        detailTotalSettingDto.packageName ?: ""
                                    )

                                    else -> Pair("", "")
                                }

                                ModifyListStringItem(
                                    item,
                                    initContent,
                                    content,
                                    onValueChanged = { contents ->
                                        onValueChanged(
                                            viewModel,
                                            detailTotalSettingDto,
                                            item,
                                            content = contents
                                        )
                                    }
                                )
                            }

                            SettingFieldEnum.FieldType.FILE_TYPE -> {
                                ModifyListFileItem(
                                    detailTotalSettingDto,
                                    item,
                                    when (item) {
                                        SettingFieldEnum.IMAGE_URL -> detailTotalSettingDto.imageUrl
                                            ?: ""

                                        else -> ""
                                    }
                                )
                            }
                        }
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                            thickness = 1.dp,
                        )
                    }
                }
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

            TwoButtonAlertDialog(
                isShowDialog = isShowConfirmDialog,
                onClickConfirm = {
                    when (isConfirmType) {
                        AdminViewModel.ConfirmType.CONFIRM -> {
                            viewModel.setTotalSettingChanged()
                        }

                        else -> {
                            navController.popBackStack()
                        }
                    }
                    isShowConfirmDialog = false
                },
                onClickCancel = { isShowConfirmDialog = false },
                title = context.getString(R.string.common_error_title_notice_msg),
                content = when (isConfirmType) {
                    AdminViewModel.ConfirmType.CONFIRM -> context.getString(R.string.str_confirm_save)
                    else -> context.getString(R.string.str_cancel_save)
                }
            )
        }
    }
}

@Composable
fun ModifyListBooleanItem(
    initChecked: Boolean,
    settingFieldEnum: SettingFieldEnum,
    isChecked: Boolean,
    onValueChanged: (Boolean) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        ) {

            Row() {
                if (initChecked != isChecked) {
                    Icon(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(16.dp)
                            .align(Alignment.CenterVertically),
                        imageVector = Icons.Default.Edit,
                        contentDescription = "isEditIcon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "${settingFieldEnum.title}(${settingFieldEnum.fieldName})",
                    fontSize = 14.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                text = settingFieldEnum.description,
                fontSize = 12.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        CommonSwitch(
            modifier = Modifier.align(Alignment.CenterVertically),
            isChecked = isChecked,
            onCheckedChanged = {
                onValueChanged(it)
            })
    }
}

@Composable
fun ModifyListStringItem(
    settingFieldEnum: SettingFieldEnum,
    initContent: String,
    content: String,
    onValueChanged: (String) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Row() {
                if (initContent != content) {
                    Icon(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(16.dp)
                            .align(Alignment.CenterVertically),
                        imageVector = Icons.Default.Edit,
                        contentDescription = "isEditIcon"
                    )
                }
                Text(
                    text = "${settingFieldEnum.title}(${settingFieldEnum.fieldName})",
                    fontSize = 14.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = settingFieldEnum.description,
                fontSize = 12.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(4.dp))
            CommonTextField(
                value = content,
                onValueChanged = { contents -> onValueChanged(contents) },
                singleLine = settingFieldEnum != SettingFieldEnum.DESCRIPTION,
                isTrailingVisible = initContent != content,
                trailIcon = {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "refresh",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
                trailButtonClick = {
                    onValueChanged(initContent)
                }
            )
        }
    }
}

private fun onValueChanged(
    viewModel: AdminViewModel,
    totalSettingDto: UiTotalSettingDto,
    settingFieldEnum: SettingFieldEnum,
    content: Any
) {
    val newDto = when (settingFieldEnum) {
        SettingFieldEnum.VISIBLE -> (content as? Boolean)?.let {
            val newIsInitEnabled = if (!it) false else totalSettingDto.isInitEnabled
            totalSettingDto.copy(
                isVisible = it,
                isInitEnabled = newIsInitEnabled
            )
        }
        SettingFieldEnum.ENABLED -> (content as? Boolean)?.let { totalSettingDto.copy(isInitEnabled = it) }
        SettingFieldEnum.PACKAGE -> (content as? String)?.let { totalSettingDto.copy(packageName = it) }
        SettingFieldEnum.IMAGE_URL -> (content as? String)?.let { totalSettingDto.copy(imageUrl = it) }
        SettingFieldEnum.DESCRIPTION -> (content as? String)?.let { totalSettingDto.copy(description = it) }
        SettingFieldEnum.DISPLAY_NAME -> (content as? String)?.let {
            totalSettingDto.copy(
                displayName = it
            )
        }

        else -> null
    }

    newDto?.let { viewModel.setDetailTotalSettingDto(it) }
}

@Composable
fun ModifyListFileItem(
    totalSettingDto: UiTotalSettingDto,
    settingFieldEnum: SettingFieldEnum,
    content: String,
    onValueChanged: (UiTotalSettingDto) -> Unit = {}
) {
    // TODO : 아이콘 등 이미지를 업로드 하는 방법 고안
}

@Composable
@Preview(showBackground = true)
fun ModifyListBooleanItemPreview() {
    ModifyListBooleanItem(
        initChecked = false,
        settingFieldEnum = SettingFieldEnum.VISIBLE,
        isChecked = true,
    )
}

@Composable
@Preview(showBackground = true)
fun ModifyListStringItemPreview() {
    ModifyListStringItem(
        initContent = "initContents",
        settingFieldEnum = SettingFieldEnum.DISPLAY_NAME,
        content = "contents",
    )
}
package com.grusie.presentation.ui.setting

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.grusie.core.common.SettingType
import com.grusie.core.utils.LogType
import com.grusie.domain.data.DomainPersonalSettingDto
import com.grusie.domain.data.DomainTotalSettingDto
import com.grusie.presentation.R
import com.grusie.presentation.Routes
import com.grusie.presentation.data.setting.MergedSetting
import com.grusie.presentation.data.setting.totalmenu.TOTAL_APP_SETTING
import com.grusie.presentation.mapper.toUi
import com.grusie.presentation.ui.base.BaseEventState
import com.grusie.presentation.ui.base.BaseUiState
import com.grusie.presentation.ui.common.CircleProgressBar
import com.grusie.presentation.ui.common.CommonAppIcon
import com.grusie.presentation.ui.common.CommonSwitch
import com.grusie.presentation.ui.common.CommonTitleBar
import com.grusie.presentation.ui.common.OneButtonAlertDialog
import com.grusie.presentation.utils.getErrorMsg
import kotlinx.coroutines.launch

@Composable
fun SettingScreen(
    navController: NavHostController,
    viewModel: SettingViewModel = hiltViewModel()
) {
    // 뒤로가기 버튼 눌렀을 때 main으로 이동
    BackHandler {
        navController.popBackStack("main", inclusive = false)
    }

    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current
    var errorMsg by remember { mutableStateOf("") }
    val settingMergedList = viewModel.settingMergedList.collectAsState().value
    var isShowErrorDialog by remember { mutableStateOf(false) }
    val settingSwitchStates by viewModel.settingSwitchStates.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.eventState.collect { eventState ->
            if (eventState != null) {
                when (eventState) {
                    is BaseEventState.Error -> {
                        errorMsg = eventState.errorMsg
                        isShowErrorDialog = true
                    }

                    is BaseEventState.Navigate -> {
                        try {
                            navController.navigate(eventState.route) {
                                if (eventState.includeBackStack) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        } catch (e: Exception) {
                            viewModel.log(
                                LogType.LOG_TYPE_E,
                                "SettingScreen Navigate Error",
                                e.getErrorMsg(context)
                            )
                            isShowErrorDialog = true
                        }
                    }

                    is BaseEventState.BroadCast -> {
                        context.sendBroadcast(Intent(eventState.broadCastAction))
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CommonTitleBar(
                title = context.getString(R.string.title_setting),
                navController = navController
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            val firstAppSettingIndex = settingMergedList.indexOfFirst { it.totalSetting.type == SettingType.APP }

            LazyColumn() {
                itemsIndexed(settingMergedList) { index, settingItem ->
                    val isRadioSelected =
                        settingSwitchStates[settingItem.totalSetting.menuId] ?: false

                    if (settingItem.totalSetting.type == SettingType.APP) {
                        if (index == firstAppSettingIndex) {

                            CustomItem(
                                title = if (viewModel.auth.currentUser != null) context.getString(R.string.str_sign_out) else context.getString(
                                    R.string.str_login
                                ),
                                icon = painterResource(R.drawable.ic_sign_out),
                                onClick = {
                                    if (viewModel.auth.currentUser != null) viewModel.signOut() else viewModel.setEventState(
                                        BaseEventState.Navigate(Routes.LOGIN, true)
                                    )
                                }
                            )

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f),
                                thickness = 1.dp,
                            )
                        }

                        AppSettingListItem(viewModel, settingItem, isRadioSelected)

                    } else {
                        TotalSettingListItem(viewModel, settingItem, isRadioSelected)
                    }

                    if (settingMergedList.last() != settingItem) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                            thickness = 1.dp,
                        )
                    }
                }
            }

            when (uiState) {
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
        }
    }
}

@Composable
fun CustomItem(
    title: String = "",
    description: String = "",
    onClick: () -> Unit = {},
    icon: Painter? = null,
    isRadioButtonVisible: Boolean = false,
    isRadioButtonChecked: Boolean = false,
    onRadioChecked: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 70.dp)
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        ) {
            if(icon != null) {
                Icon(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    painter = icon,
                    contentDescription = "settingDrawable",
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.width(8.dp))
            }

            Column(
                Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
            ) {
                Text(
                    text = title,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    overflow = TextOverflow.Ellipsis
                )

                if(description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = description,
                        maxLines = 2,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontSize = 14.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (isRadioButtonVisible) {
                CommonSwitch(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    isChecked = isRadioButtonChecked,
                    onCheckedChanged = {
                        onRadioChecked()
                    }
                )
            }
        }
    }
}

@Composable
fun TotalSettingListItem(
    viewModel: SettingViewModel? = null,
    mergedSetting: MergedSetting,
    isRadioSelected: Boolean = false
) {
    val totalSetting = mergedSetting.totalSetting
    val personalSetting = mergedSetting.personalSetting

    var totalAppSettingEnum: TOTAL_APP_SETTING? = null

    if (totalSetting.type == SettingType.GENERAL) {
        // 앱에 정의되어 있지 않은 설정 값일 경우 화면에 표시하지 않는다.
        totalAppSettingEnum = mergedSetting.totalSetting.toUi().totalAppSettingEnum ?: run { return }
    }

    val settingMenu = totalAppSettingEnum!!.settingMenu

    val scope = rememberCoroutineScope()

    if (totalSetting.isVisible) {
        Box(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 12.dp)
                .defaultMinSize(minHeight = 70.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            ) {
                Icon(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    painter = painterResource(settingMenu.drawableResId),
                    contentDescription = "settingDrawable",
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.width(8.dp))

                Column(
                    Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1f)
                ) {
                    Text(
                        text = totalSetting.displayName,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = totalSetting.description,
                        maxLines = 2,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontSize = 14.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                if (settingMenu.radioButtonVisible) {
                    CommonSwitch(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        isChecked = isRadioSelected,
                        onCheckedChanged = {
                            scope.launch {
                                viewModel?.onSettingRadioButtonChanged(totalAppSettingEnum.menuId, !isRadioSelected)
                            }
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun AppSettingListItem(
    viewModel: SettingViewModel? = null,
    mergedSetting: MergedSetting,
    isRadioSelected: Boolean = false
) {
    val appSetting = mergedSetting.totalSetting
    val personalSetting = mergedSetting.personalSetting

    val scope = rememberCoroutineScope()

    if (appSetting.isVisible) {
        Box(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 12.dp)
                .defaultMinSize(minHeight = 50.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            ) {
                CommonAppIcon(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(24.dp),
                    imageUrl = appSetting.imageUrl,
                    isTintUse = appSetting.isTintUse
                )
                Spacer(Modifier.width(8.dp))

                Text(
                    modifier = Modifier.align(Alignment.CenterVertically).weight(1f),
                    text = appSetting.displayName,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.width(8.dp))

                CommonSwitch(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    isChecked = isRadioSelected,
                    onCheckedChanged = {
                        scope.launch {
                            viewModel?.onSettingRadioButtonChanged(
                                appSetting.menuId,
                                !isRadioSelected
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xffffffff)
fun TotalSettingListItemPreview() {
    TotalSettingListItem(
        mergedSetting = MergedSetting(
            DomainTotalSettingDto(
                menuId = 100,
                isVisible = true,
                isInitEnabled = true,
                description = "테스트 세팅 설명입니다. 2줄까지 가능하기에 길게 한 번 넣어보도록 하죠 이게 과연 중앙이 맞는지 의심되는군요 중앙정렬 치고는 위로 좀 올라와 있는 거 같은데... 어이없네요",
                displayName = "얜 맥스라인 1이예요 근데 ellipsize 넣어야겠네, 얜 맥스라인 1이예요 근데 ellipsize 넣어야겠네"
            ), DomainPersonalSettingDto()
        )
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xffffffff)
fun AppSettingListItemPreview() {
    AppSettingListItem(
        mergedSetting = MergedSetting(
            DomainTotalSettingDto(
                isVisible = true,
                isInitEnabled = true,
                description = "테스트 세팅 설명입니다. 2줄까지 가능하기에 길게 한 번 넣어보도록 하죠 이게 과연 중앙이 맞는지 의심되는군요 중앙정렬 치고는 위로 좀 올라와 있는 거 같은데... 어이없네요",
                displayName = "얜 맥스라인 1이예요 근데 ellipsize 넣어야겠네, 얜 맥스라인 1이예요 근데 ellipsize 넣어야겠네",
                packageName = "com.grusie.readingnoti",
                type = SettingType.APP
            ), DomainPersonalSettingDto()
        )
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xffffffff)
fun CustomListItemPreview() {
    CustomItem(
        title = "로그아웃",
        icon = painterResource(R.drawable.ic_sign_out)
    )
}
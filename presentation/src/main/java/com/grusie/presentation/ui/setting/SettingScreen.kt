package com.grusie.presentation.ui.setting

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.grusie.core.common.SettingType
import com.grusie.domain.data.DomainPersonalSettingDto
import com.grusie.presentation.R
import com.grusie.presentation.data.setting.MergedSetting
import com.grusie.presentation.data.setting.totalmenu.TOTAL_APP_SETTING
import com.grusie.presentation.data.setting.totalmenu.UiTotalSettingDto
import com.grusie.presentation.ui.common.CircleProgressBar
import com.grusie.presentation.ui.common.CommonSwitch
import com.grusie.presentation.ui.common.CommonTitleBar
import com.grusie.presentation.ui.common.OneButtonAlertDialog
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
                    is SettingEventState.Error -> {
                        errorMsg = eventState.errorMsg
                        isShowErrorDialog = true
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
            LazyColumn() {
                var isFirstAppSetting = false
                items(settingMergedList) { settingItem ->
                    val isRadioSelected =
                        settingSwitchStates[settingItem.totalSetting.menuId] ?: false

                    if (settingItem.totalSetting.type == SettingType.APP) {
                        if (!isFirstAppSetting) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f),
                                thickness = 1.dp,
                            )
                            isFirstAppSetting = true
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
                is SettingUiState.Loading -> {
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
        totalAppSettingEnum = mergedSetting.totalSetting.totalAppSettingEnum ?: run { return }
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
                                totalAppSettingEnum.onRadioChanged(viewModel, !isRadioSelected)
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
                .defaultMinSize(minHeight = 70.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            ) {
                Image(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(24.dp),
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(appSetting.imageUrl)
                            .diskCachePolicy(CachePolicy.DISABLED)
                            .memoryCachePolicy(CachePolicy.DISABLED)
                            .placeholder(R.drawable.ic_image_placeholder)
                            .build()),
                    contentDescription = "app_icon",
                    colorFilter = if (appSetting.isTintUse) ColorFilter.tint(MaterialTheme.colorScheme.onBackground) else null
                )
                Spacer(Modifier.width(8.dp))

                Column(
                    Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1f)
                ) {
                    Text(
                        text = appSetting.displayName,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                }

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
fun SettingListCardPreview() {
    TotalSettingListItem(
        mergedSetting = MergedSetting(
            UiTotalSettingDto(
                isVisible = true,
                isInitEnabled = true,
                description = "테스트 세팅 설명입니다. 2줄까지 가능하기에 길게 한 번 넣어보도록 하죠 이게 과연 중앙이 맞는지 의심되는군요 중앙정렬 치고는 위로 좀 올라와 있는 거 같은데... 어이없네요",
                displayName = "얜 맥스라인 1이예요 근데 ellipsize 넣어야겠네, 얜 맥스라인 1이예요 근데 ellipsize 넣어야겠네",
                totalAppSettingEnum = TOTAL_APP_SETTING.TOTAL_NOTI_ENABLED
            ), DomainPersonalSettingDto()
        )
    )
}
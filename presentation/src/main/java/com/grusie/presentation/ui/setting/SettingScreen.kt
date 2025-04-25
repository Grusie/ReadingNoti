package com.grusie.presentation.ui.setting

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.grusie.presentation.R
import com.grusie.presentation.data.setting.totalmenu.TOTAL_APP_SETTING
import com.grusie.presentation.data.setting.totalmenu.UiTotalSettingDto
import com.grusie.presentation.ui.common.CircleProgressBar
import com.grusie.presentation.ui.common.CommonTitleBar

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
    var totalSettingList by remember { mutableStateOf(listOf<UiTotalSettingDto>()) }

    LaunchedEffect(Unit) {
        viewModel.eventState.collect { eventState ->
            if (eventState != null) {
                when (eventState) {
                    is SettingEventState.Success -> {
                        if (eventState.data != null) {
                            when (eventState.data) {
                                is List<*> -> {
                                    totalSettingList =
                                        eventState.data.map { it as UiTotalSettingDto }
                                }
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
                items(totalSettingList) { settingItem ->
                    SettingListCard(settingItem)
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                        thickness = 1.dp,
                    )
                }
            }

            when (uiState) {
                is SettingUiState.Loading -> {
                    CircleProgressBar()
                }
            }
        }
    }
}

@Composable
fun SettingListCard(totalSettingData: UiTotalSettingDto) {

    // 앱에 정의되어 있지 않은 설정 값일 경우 화면에 표시하지 않는다.
    val totalAppSettingEnum = totalSettingData.totalAppSettingEnum ?: run { return }
    val settingMenu = totalAppSettingEnum.settingMenu

    if (totalSettingData.isVisible) {
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
                        .weight(1f)) {
                    Text(
                        text = totalSettingData.displayName,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = totalSettingData.description,
                        maxLines = 2,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontSize = 14.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                if (settingMenu.radioButtonVisible) {
                    var isRadioSelected by remember { mutableStateOf(totalSettingData.isInitEnabled) }
                    Switch(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.surface, // thumb은 일반 배경색처럼 부드럽게
                            checkedTrackColor = MaterialTheme.colorScheme.primary, // 트랙(배경)은 primary 컬러
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.Gray.copy(alpha = 0.4f)
                        ),
                        checked = isRadioSelected,
                        onCheckedChange = {
//                        isRadioSelected = settingMenu.onRadioChanged()
                        }
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xffffffff)
fun SettingListCardPreview() {
    SettingListCard(
        UiTotalSettingDto(
            isVisible = true,
            isInitEnabled = true,
            description = "테스트 세팅 설명입니다. 2줄까지 가능하기에 길게 한 번 넣어보도록 하죠 이게 과연 중앙이 맞는지 의심되는군요 중앙정렬 치고는 위로 좀 올라와 있는 거 같은데... 어이없네요",
            displayName = "얜 맥스라인 1이예요 근데 ellipsize 넣어야겠네, 얜 맥스라인 1이예요 근데 ellipsize 넣어야겠네",
            totalAppSettingEnum = TOTAL_APP_SETTING.TOTAL_NOTI_ENABLED
        )
    )
}
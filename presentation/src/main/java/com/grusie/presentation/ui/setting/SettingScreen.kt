package com.grusie.presentation.ui.setting

import android.widget.Space
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.grusie.domain.data.DomainTotalSettingDto
import com.grusie.presentation.data.setting.totalmenu.TOTAL_APP_SETTING
import com.grusie.presentation.data.setting.totalmenu.UiTotalSettingDto
import com.grusie.presentation.ui.common.CircleProgressBar

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

    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            LazyColumn() {
                items(totalSettingList) { settingItem ->
                    SettingListCard(settingItem)
                    Spacer(
                        modifier = Modifier
                            .height(8.dp)
                            .fillMaxWidth()
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
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .padding(vertical = 16.dp, horizontal = 8.dp)
                .defaultMinSize(minHeight = 70.dp)
        ) {
            Row(
                Modifier
                    .align(Alignment.Center)) {
                Icon(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    painter = painterResource(settingMenu.drawableResId),
                    contentDescription = "settingDrawable"
                )
                Spacer(Modifier.width(8.dp))

                Column(Modifier.align(Alignment.CenterVertically)) {
                    Text(
                        text = totalSettingData.displayName,
                        maxLines = 1,
                        color = Color.Black,
                        fontSize = 16.sp,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = totalSettingData.description,
                        maxLines = 2,
                        color = Color.Gray,
                        fontSize = 14.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                }

//                if(settingMenu.radioButtonVisible) {
//                    var isRadioSelected by remember { mutableStateOf(totalSettingData.isInitEnabled) }
//                    RadioButton(selected = isRadioSelected, onClick = {
//                        isRadioSelected = settingMenu.onRadioChanged()
//                    })
//                }
            }
        }
    }
}

@Composable
@Preview()
fun SettingListCardPreview() {
    SettingListCard(
        UiTotalSettingDto(
            isVisible = true,
            isInitEnabled = true,
            description = "테스트 세팅 설명입니다. ",
            displayName = "얜 맥스라인 1이예요 근데 ellipsize 넣어야겠네, 얜 맥스라인 1이예요 근데 ellipsize 넣어야겠네",
            totalAppSettingEnum = TOTAL_APP_SETTING.TOTAL_NOTI_ENABLED
        )
    )
}
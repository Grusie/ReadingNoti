package com.grusie.presentation.ui.main

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.grusie.core.common.TotalMenu
import com.grusie.core.utils.toTimeString
import com.grusie.domain.data.DomainMsgData
import com.grusie.domain.data.DomainTotalSettingDto
import com.grusie.presentation.R
import com.grusie.presentation.Routes
import com.grusie.presentation.ui.base.BaseEventState
import com.grusie.presentation.ui.base.BaseUiState
import com.grusie.presentation.ui.common.CircleProgressBar
import com.grusie.presentation.ui.common.CommonAppIcon
import com.grusie.presentation.ui.common.CommonMsgDetail
import com.grusie.presentation.ui.common.CommonTitleBar
import com.grusie.presentation.ui.common.TitleButtonItem
import com.grusie.presentation.ui.common.TitleIcon

@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val context = LocalContext.current
        val isAdmin = viewModel.isAdmin.collectAsState().value
        val mergedGeneralSettingMap = viewModel.mergedGeneralSettingMap.collectAsState().value
        val mergedAppSettingMap = viewModel.mergedAppSettingMap.collectAsState().value
        val msgDataList = viewModel.msgDataList.collectAsState().value

        val uiState = viewModel.uiState.collectAsState().value
        var errorMsg by remember { mutableStateOf("") }
        var isShowErrorDialog by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            viewModel.eventState.collect { eventState ->
                if (eventState != null) {
                    when (eventState) {
                        is BaseEventState.Error -> {
                            errorMsg = eventState.errorMsg
                            isShowErrorDialog = true
                        }

                        is BaseEventState.Toast -> {
                            Toast.makeText(context, eventState.toastMsg, Toast.LENGTH_SHORT).show()
                        }

                        is BaseEventState.Navigate -> {
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
                    title = context.getString(R.string.title_main),
                    navController = navController,
                    leftButton = null,
                    rightButton = buildList {
                        add(
                            TitleButtonItem(
                                titleIcon = TitleIcon.Vector(Icons.Default.Settings),
                                onClick = { viewModel.setEventState(BaseEventState.Navigate(Routes.SETTING)) }
                            )
                        )
                        if (isAdmin) {
                            add(
                                TitleButtonItem(
                                    titleIcon = TitleIcon.Vector(Icons.Default.Build),
                                    onClick = {
                                        viewModel.setEventState(
                                            BaseEventState.Navigate(
                                                Routes.ADMIN
                                            )
                                        )
                                    }
                                )
                            )
                        }
                    },
                    isTitleCenter = true
                )
            }

        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                val isEnabled = viewModel.isEnabled.collectAsState().value
                val verticalScroll = rememberScrollState()
                var isMsgDetailViewVisible by remember { mutableStateOf(false) }
                var msgDetail: DomainMsgData? by remember { mutableStateOf(null) }

                val activity = LocalActivity.current

                BackHandler {
                    if(isMsgDetailViewVisible) {
                        isMsgDetailViewVisible = false
                    } else {
                        val popped = navController.popBackStack()
                        if (!popped) {
                            activity?.finish() // 앱 종료
                        }
                    }
                }

                Column(
                    Modifier
                        .verticalScroll(verticalScroll)
                        .padding(top = 60.dp)
                        .fillMaxSize()
                ) {
                    AuraPulseCircle(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        isEnabled = isEnabled,
                        onChangeEnabled = {
                            viewModel.changeEnabled(
                                TotalMenu.COLLECT_NOTI_ENABLED.menuId,
                                it
                            )
                        },
                        color = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                    )

                    Spacer(modifier = Modifier.height(60.dp))

                    Row(modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 12.dp, bottom = 4.dp)
                        .clickable {
                            viewModel.setEventState(BaseEventState.Navigate(Routes.MSG_APP_LIST))
                        }
                        .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = context.getString(R.string.str_see_all), fontSize = 16.sp)
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            "see all msg icon"
                        )
                    }
                    LazyRow() {
                        item {
                            Spacer(modifier = Modifier.width(20.dp))
                        }
                        items(
                            msgDataList
                        ) { item ->
                            MsgListItem(
                                msgData = item,
                                appSetting = mergedAppSettingMap[item.menuId]?.totalSetting
                                    ?: DomainTotalSettingDto(
                                        menuId = item.menuId,
                                        displayName = "UnKnownApp"
                                    ),
                                onClick = {
                                    msgDetail = it
                                    isMsgDetailViewVisible = true
                                }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                    }
                }

                if(isMsgDetailViewVisible) {
                    msgDetail?.let { CommonMsgDetail(it) {
                        isMsgDetailViewVisible = false
                    } }
                }

                when (uiState) {
                    BaseUiState.Loading -> {
                        CircleProgressBar()
                    }
                }
            }
        }
    }
}

@Composable
fun AuraPulseCircle(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    pulseCount: Int = 2,
    isEnabled: Boolean,
    onChangeEnabled: (Boolean) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "AuraPulse")

    val animations = List(pulseCount) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3000, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "Pulse$index"
        )
    }

    // 중심 원 크기 (dp)
    val centerButtonSizeDp = maxOf(LocalConfiguration.current.screenHeightDp.dp * 0.2f, 160.dp)
    val auraMaxSizeDp = centerButtonSizeDp * 0.65f

    val density = LocalDensity.current

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (isEnabled) {
            Canvas(modifier = Modifier) {
                val maxRadiusPx = with(density) { auraMaxSizeDp.toPx() }

                animations.forEach { anim ->
                    drawCircle(
                        color = color.copy(alpha = 1 - anim.value),
                        radius = maxRadiusPx * anim.value
                    )
                }
            }
        }

        OnOffButton(Modifier, centerButtonSizeDp, isEnabled, onChangeEnabled, color)
    }
}

@Composable
fun OnOffButton(
    modifier: Modifier = Modifier,
    buttonSize: Dp,
    isEnabled: Boolean,
    onChangeEnabled: (Boolean) -> Unit,
    color: Color
) {
    Box(
        modifier = modifier
            .size(buttonSize)
            .clip(CircleShape)
            .background(color)
            .clickable { onChangeEnabled(!isEnabled) },
        contentAlignment = Alignment.Center
    ) {
        Text(text = if (isEnabled) "ON" else "OFF", fontSize = 40.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MsgListItem(
    msgData: DomainMsgData,
    appSetting: DomainTotalSettingDto,
    onClick: (DomainMsgData) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(end = 8.dp)
            .background(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            )
            .clip(shape = RoundedCornerShape(8.dp))
            .size(200.dp, 200.dp)
            .clickable {
                onClick(msgData)
            }
            .padding(8.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CommonAppIcon(
                    modifier = Modifier.size(24.dp),
                    imageUrl = appSetting.imageUrl,
                    isTintUse = appSetting.isTintUse
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = appSetting.displayName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            if(msgData.subTitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = msgData.subTitle,
                    maxLines = 1,
                    fontSize = 14.sp,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            if(msgData.title.isNotEmpty()) {
                Spacer(modifier = Modifier.height(if(msgData.subTitle.isNotEmpty()) 2.dp else 4.dp))
                Text(
                    text = msgData.title,
                    maxLines = 1,
                    fontSize = 14.sp,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                text = msgData.content,
                overflow = TextOverflow.Ellipsis,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 20.sp,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = msgData.timeStamp.toTimeString(),
                color = MaterialTheme.colorScheme.outline,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
@Preview()
fun MsgListItemPreview() {
    MsgListItem(
        msgData = DomainMsgData(
            menuId = 200,
            title = "채팅방 이름",
            subTitle = "내가 보냈다. 프로필 이미지까지 받아오는 건 오바인 것 같고 힘들 거 같으니 그냥 이걸로 대체하겠다.",
            content = "내용입니다.",
            timeStamp = System.currentTimeMillis()
        ),
        appSetting = DomainTotalSettingDto(
            displayName = "카카오톡"
        )
    ) {}
}

@Composable
@Preview(showBackground = true, heightDp = 200)
fun AuraPulseCirclePreview() {

    AuraPulseCircle(isEnabled = true) { }

}
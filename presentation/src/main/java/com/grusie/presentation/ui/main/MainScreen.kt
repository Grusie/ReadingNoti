package com.grusie.presentation.ui.main

import android.content.Context
import android.provider.Settings
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.grusie.core.common.TotalMenu
import com.grusie.presentation.R
import com.grusie.presentation.Routes
import com.grusie.presentation.ui.base.BaseEventState
import com.grusie.presentation.ui.base.BaseUiState
import com.grusie.presentation.ui.common.CircleProgressBar
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
        val personalSettingList = viewModel.personalSettingList.collectAsState().value
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
                        if(isAdmin) {
                            add(
                                TitleButtonItem(
                                    titleIcon = TitleIcon.Vector(Icons.Default.Build),
                                    onClick = { viewModel.setEventState(BaseEventState.Navigate(Routes.ADMIN)) }
                                )
                            )
                        }
                    },
                    isTitleCenter = true
                )
            }

        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                val isEnabled = viewModel.isTotalNotiEnabled.collectAsState().value

                AuraPulseCircle(
                    modifier = Modifier.align(Alignment.Center),
                    isEnabled = isEnabled,
                    onChangeEnabled = { viewModel.changeEnabled(TotalMenu.TOTAL_NOTI_ENABLED.menuId, it) },
                    color = if(isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                )

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
    val centerButtonSizeDp = LocalConfiguration.current.screenHeightDp.dp * 0.2f
    val auraMaxSizeDp = centerButtonSizeDp * 0.65f

    val density = LocalDensity.current

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if(isEnabled) {
            Canvas(modifier = Modifier.fillMaxSize()) {
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
        Text(text = if(isEnabled) "ON" else "OFF", fontSize = 40.sp, fontWeight = FontWeight.Bold)
    }
}
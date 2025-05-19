package com.grusie.presentation.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.grusie.presentation.R
import com.grusie.presentation.utils.UiUtil

data class TitleButtonItem(
    @DrawableRes val iconRes: Int? = null,
    val onClick: () -> Unit,
    val text: String = "",
    val textColor: Color? = null,
    val iconTint: Color? = null
)

@Composable
fun CommonTitleBar(
    titleModifier: Modifier = Modifier,
    titleTextModifier: Modifier = Modifier,
    title: String,
    navController: NavController,
    leftButton: List<TitleButtonItem>? = listOf(
        TitleButtonItem(
            R.drawable.ic_back_black,
            { navController.popBackStack() })
    ),
    rightButton: List<TitleButtonItem>? = null,
    isTitleCenter: Boolean = false
) {
    Column(
        titleModifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (isTitleCenter) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 20.sp,
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            } else {
                // 왼쪽 버튼 옆에 붙도록 패딩 넣기
                Text(
                    modifier = titleTextModifier
                        .align(Alignment.CenterStart)
                        .padding(start = (leftButton?.size ?: 0) * 40.dp + 12.dp),
                    fontSize = 20.sp,
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start
                )
            }
            Row(
                modifier = titleTextModifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Start
                ) {
                    leftButton?.forEach { titleButtonItem ->
                        val clickHelper = remember { UiUtil.DebounceClickHelper() }

                        IconButton(
                            modifier = Modifier.size(40.dp), onClick = {
                                if (clickHelper.canClick()) {
                                    titleButtonItem.onClick()
                                }
                            }) {
                            titleButtonItem.iconRes?.let {
                                Icon(
                                    painterResource(it),
                                    contentDescription = "left icon",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            } ?: run {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clickable { titleButtonItem.onClick() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = titleButtonItem.text,
                                        color = titleButtonItem.textColor
                                            ?: MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.End
                ) {
                    rightButton?.forEach { titleButtonItem ->
                        IconButton(modifier = Modifier
                            .size(40.dp)
                            .padding(0.dp), onClick = {
                            titleButtonItem.onClick()
                        }) {
                            titleButtonItem.iconRes?.let {
                                Icon(
                                    painterResource(it),
                                    contentDescription = "right icon",
                                    tint = titleButtonItem.iconTint ?: MaterialTheme.colorScheme.onSurface
                                )
                            } ?: run {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = titleButtonItem.text,
                                        color = titleButtonItem.textColor
                                            ?: MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
    }
}

@Composable
@Preview
fun TitleBarPreView() {
    CommonTitleBar(
        title = "타이틀",
        navController = rememberNavController(),
        leftButton = listOf(
            TitleButtonItem(null, {}, "취소", MaterialTheme.colorScheme.onSurface),
        ),
        rightButton = listOf(
            TitleButtonItem(
                R.drawable.ic_reboot_black,
                {},
                "취소",
                MaterialTheme.colorScheme.onSurface
            ),
            TitleButtonItem(null, {}, "확인", MaterialTheme.colorScheme.onSurface)
        ),
        isTitleCenter = true
    )
}
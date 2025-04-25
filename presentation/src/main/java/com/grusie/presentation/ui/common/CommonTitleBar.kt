package com.grusie.presentation.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.grusie.presentation.R
import com.grusie.presentation.utils.UiUtil

data class TitleButtonItem(
    @DrawableRes val iconRes: Int,
    val onClick: () -> Unit
)

@Composable
fun CommonTitleBar(
    titleModifier: Modifier = Modifier,
    titleTextModifier: Modifier = Modifier,
    title: String,
    navController: NavController,
    leftButton: List<TitleButtonItem>? = listOf(TitleButtonItem(R.drawable.ic_back_black) { navController.popBackStack() }),
    rightButton: List<TitleButtonItem>? = null
) {
    Column(
        titleModifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
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
                        Icon(
                            painterResource(titleButtonItem.iconRes),
                            contentDescription = "left icon",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Text(
                modifier = titleTextModifier,
                fontSize = 20.sp,
                text = title,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End
            ) {
                rightButton?.forEach { titleButtonItem ->
                    IconButton(modifier = Modifier
                        .size(40.dp)
                        .padding(0.dp), onClick = {
                        titleButtonItem.onClick()
                    }) {
                        Icon(
                            painterResource(titleButtonItem.iconRes),
                            contentDescription = "right icon",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
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
        title = "타이틀", navController = rememberNavController(), rightButton = listOf(
            TitleButtonItem(R.drawable.ic_back_black, {})
        )
    )
}
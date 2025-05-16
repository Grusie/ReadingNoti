package com.grusie.presentation.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


private var lastClickTime = 0L

// 버튼 클릭 시 debounce를 주는 확장함수
fun Modifier.debounceClickable(
    debounceTime: Long = 1000L,
    onClick: () -> Unit
): Modifier = composed {
    clickable {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > debounceTime) {
            lastClickTime = currentTime
            onClick()
        }
    }
}

@Composable
fun CommonSwitch(
    modifier: Modifier = Modifier,
    isChecked: Boolean,
    onCheckedChanged: (Boolean) -> Unit = {}
) {
    Switch(
        modifier = modifier,
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.surface,
            checkedTrackColor = MaterialTheme.colorScheme.primary,
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = Color.Gray.copy(alpha = 0.4f)
        ),
        checked = isChecked,
        onCheckedChange = { onCheckedChanged(it) }
    )
}

@Composable
fun CommonTextField(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(
            MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(10.dp)
        ),
    value: String,
    onValueChanged: (String) -> Unit = {},
    isTrailingVisible: Boolean = false,
    trailButtonClick: () -> Unit = {},
    singleLine: Boolean = true,
    textStyle: TextStyle = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight(400),
        color = MaterialTheme.colorScheme.onSurface,
    ),
    trailIcon: @Composable () -> Unit = @Composable {}
) {
    BasicTextField(
        modifier = modifier,
        value = value,
        onValueChange = { contents -> onValueChanged(contents) },
        singleLine = singleLine,
        textStyle = textStyle,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .defaultMinSize(minHeight = 48.dp)
                    .padding(horizontal = 8.dp)
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    innerTextField()
                }

                AnimatedVisibility(
                    visible = isTrailingVisible,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    IconButton(
                        modifier = Modifier.size(40.dp), onClick = {
                            trailButtonClick()
                        }) {
                        trailIcon()
                    }
                }
            }
        }
    )
}

@Composable
@Preview(showBackground = false)
fun CommonTextFieldPreview() {
    CommonTextField(
        value = "contents",
        onValueChanged = {}
    )
}

@Composable
@Preview(showBackground = true)
fun CommonSwitchPreview() {
    Row(modifier = Modifier.padding(8.dp)) {
        CommonSwitch(
            isChecked = false,
            onCheckedChanged = {}
        )

        Spacer(Modifier.width(8.dp))
        CommonSwitch(
            isChecked = true,
            onCheckedChanged = {}
        )
    }
}
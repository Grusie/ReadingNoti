package com.grusie.presentation.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.grusie.core.utils.toTimeString
import com.grusie.domain.data.DomainMsgData
import com.grusie.presentation.R


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
    modifier: Modifier = Modifier,
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
    trailIcon: @Composable () -> Unit = @Composable {},
    isEnabled: Boolean = true,
    hint: String = "",
    isPasswordStyle: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    BasicTextField(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isEnabled) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.surfaceContainer.copy(
                    alpha = 0.6f
                ),
                shape = RoundedCornerShape(10.dp)
            ),
        value = value,
        onValueChange = { contents -> onValueChanged(contents) },
        singleLine = singleLine,
        textStyle = if (isEnabled) textStyle else textStyle.copy(
            color = MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.6f
            )
        ),
        enabled = isEnabled,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
        visualTransformation = if (isPasswordStyle) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = keyboardOptions,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .defaultMinSize(minHeight = 48.dp)
                    .padding(horizontal = 8.dp)
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = hint,
                            style = textStyle.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.4f
                                )
                            )
                        )
                    }
                    innerTextField()
                }

                if (isTrailingVisible)
                    IconButton(
                        modifier = Modifier.size(40.dp), onClick = {
                            trailButtonClick()
                        }) {
                        trailIcon()
                    }
            }
        }
    )
}

@Composable
fun CommonAppIcon(
    modifier: Modifier = Modifier,
    placeholder: Painter = painterResource(R.drawable.ic_image_placeholder),
    imageUrl: String? = null,
    isTintUse: Boolean = false
) {
    Image(
        modifier = modifier,
        painter = if (LocalInspectionMode.current) {
            placeholder
        } else {
            rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .build()
            )
        },
        contentDescription = "app_icon",
        contentScale = ContentScale.Crop,
        colorFilter = if (isTintUse) ColorFilter.tint(MaterialTheme.colorScheme.onBackground) else null
    )
}

@Composable
fun CommonMsgDetail(
    msgDetail: DomainMsgData,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            onDismiss()
        }.background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f)) // 배경 반투명
    ) {
        val bgColor = MaterialTheme.colorScheme.background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    vertical = LocalConfiguration.current.screenHeightDp.dp * 0.2f,
                    horizontal = LocalConfiguration.current.screenWidthDp.dp * 0.1f
                )
                .clickable( // 이 박스 안쪽은 클릭해도 dismiss 안 되게
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {}
                .drawBehind {
                    drawRoundRect(
                        color = bgColor,
                        size = size,
                        cornerRadius = CornerRadius(20.dp.toPx())
                    )
                }
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(0.4f),
                    shape = RoundedCornerShape(20.dp)
                )
                .clip(RoundedCornerShape(20.dp))
                .padding(vertical = 12.dp, horizontal = 20.dp)
        ) {
            val verticalScrollState = rememberScrollState()
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                if (msgDetail.subTitle.isNotEmpty()) {
                    Text(text = msgDetail.subTitle)
                    Spacer(modifier = Modifier.height(4.dp))
                }

                if (msgDetail.title.isNotEmpty()) {
                    Text(text = msgDetail.title)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(verticalScrollState),
                    text = msgDetail.content,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = msgDetail.timeStamp.toTimeString(),
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = false)
fun CommonTextFieldPreview() {
    CommonTextField(
        value = "contents",
        onValueChanged = {},
        isEnabled = false
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

@Composable
@Preview(showBackground = true, widthDp = 360, heightDp = 540)
fun CommonMsgDetailPreview() {
    var isVisible by remember { mutableStateOf(true) }
    
    if(isVisible) {
        CommonMsgDetail(
            DomainMsgData(
                menuId = 200,
                title = "이게 보통 Sender",
                subTitle = "이게 보통 채팅방 이름",
                content = "이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 이건 거의 내용 ",
                timeStamp = System.currentTimeMillis()
            )
        ) {
            isVisible = !isVisible
        }
    }
}
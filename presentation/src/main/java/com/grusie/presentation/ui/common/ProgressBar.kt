package com.grusie.presentation.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CircleProgressBar(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = {},
            )
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f)) // 배경 반투명
    ) {
        CircularProgressIndicator(
            modifier = modifier.align(Alignment.Center),
        )
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xffffffff)
fun CircleProgressBarPreview() {
    CircleProgressBar()
}
package com.grusie.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4CAF50), // 녹색
    secondary = Color(0xFF8BC34A), // 연두색
    tertiary = Color(0xFFCDDC39), // 라임
    background = Color(0xFFF5F5F5), // 연한 회색
    surface = Color(0xFFFFFFFF), // 흰색
    onPrimary = Color.White, // primary 위에 흰색 텍스트
    onSecondary = Color.Black, // secondary 위에 검정색 텍스트
    onTertiary = Color.Black, // tertiary 위에 검정색 텍스트
    onBackground = Color.Black, // 배경 위에 검정색 텍스트
    onSurface = Color.Black // surface 위에 검정색 텍스트
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF1EB980), // 밝은 녹색
    secondary = Color(0xFF03DAC6), // 밝은 청록
    tertiary = Color(0xFF03A9F4), // 파란색
    background = Color(0xFF121212), // 어두운 배경
    surface = Color(0xFF1E1E1E), // 어두운 표면
    onPrimary = Color.Black, // primary 위에 검정색 텍스트
    onSecondary = Color.Black, // secondary 위에 검정색 텍스트
    onTertiary = Color.Black, // tertiary 위에 검정색 텍스트
    onBackground = Color.White, // 배경 위에 흰색 텍스트
    onSurface = Color.White // surface 위에 흰색 텍스트
)

@Composable
fun ReadingNotiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
package com.example.androidtvmania.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

val Accent = Color(0xFFFF6B2C)
val Surface = Color(0xFF161A22)
val Background = Color(0xFF0B0D12)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvManiaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Accent,
            onPrimary = Color.Black,
            background = Background,
            surface = Surface,
            onBackground = Color.White,
            onSurface = Color.White,
            border = Accent.copy(alpha = 0.8f),
        ),
        content = content,
    )
}

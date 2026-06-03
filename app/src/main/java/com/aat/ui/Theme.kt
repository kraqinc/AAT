package com.aat.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AatColors = darkColorScheme(
    primary = Color(0xFFB79CFF),
    secondary = Color(0xFF7AD7FF),
    tertiary = Color(0xFF8DE6B7),
    background = Color(0xFF0C0B16),
    surface = Color(0xFF141225),
    surfaceVariant = Color(0xFF1D1A33),
    onPrimary = Color(0xFF120E24),
    onSecondary = Color(0xFF071621),
    onTertiary = Color(0xFF08150D),
    onBackground = Color(0xFFF2EFFF),
    onSurface = Color(0xFFF2EFFF),
    outline = Color(0xFF4A4564)
)

@Composable
fun AatTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AatColors,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}

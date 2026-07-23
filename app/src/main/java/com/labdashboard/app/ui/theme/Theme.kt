package com.labdashboard.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Indigo40,
    onPrimary = Color.White,
    primaryContainer = Indigo80,
    secondary = Teal40,
    background = Surface,
    surface = Surface,
    surfaceVariant = SurfaceVariant,
    onSurface = OnSurface,
    error = Red40
)

private val DarkColors = darkColorScheme(
    primary = Indigo80,
    onPrimary = IndigoDark,
    primaryContainer = IndigoDark,
    secondary = Teal80,
    background = Color(0xFF121218),
    surface = Color(0xFF1C1C24),
    surfaceVariant = Color(0xFF2A2A34),
    onSurface = Color(0xFFE6E6ED),
    error = Color(0xFFFF6B6B)
)

@Composable
fun LabDashboardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}

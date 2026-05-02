package com.example.frontendzmabt.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

val LocalDarkMode = compositionLocalOf { false }
val LocalSetDarkMode = compositionLocalOf<(Boolean) -> Unit> { {} }

private val DarkColorScheme = darkColorScheme(
    primary          = TealDark,
    onPrimary        = OnPrimaryDark,
    background       = BackgroundDark,
    surface          = SurfaceDark,
    onBackground     = OnBackgroundDark,
    onSurface        = OnBackgroundDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    surfaceVariant   = SurfaceVariantDark,
    outline          = OutlineDark,
    outlineVariant   = OutlineVariantDark,
    error            = ErrorDark,
    secondary        = SecondaryDark,
    tertiary         = TertiaryDark
)

private val LightColorScheme = lightColorScheme(
    primary          = TealLight,
    onPrimary        = OnPrimaryLight,
    background       = BackgroundLight,
    surface          = SurfaceLight,
    onBackground     = OnBackgroundLight,
    onSurface        = OnBackgroundLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    surfaceVariant   = SurfaceVariantLight,
    outline          = OutlineLight,
    outlineVariant   = OutlineVariantLight,
    error            = ErrorLight,
    secondary        = SecondaryLight,
    tertiary         = TertiaryLight
)

@Composable
fun FrontendZMABTTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}

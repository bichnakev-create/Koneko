package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LiquidDarkColorScheme = darkColorScheme(
    primary = NeonViolet,
    onPrimary = ObsidianVoid,
    primaryContainer = LiquidDarkCardElevated,
    onPrimaryContainer = NeonViolet,
    secondary = ElectricCyan,
    onSecondary = ObsidianVoid,
    secondaryContainer = LiquidDarkCard,
    onSecondaryContainer = ElectricCyan,
    tertiary = LiquidRose,
    onTertiary = ObsidianVoid,
    tertiaryContainer = LiquidDarkCard,
    onTertiaryContainer = NekoBlush,
    background = ObsidianVoid,
    onBackground = TextPrimary,
    surface = AbyssalDark,
    onSurface = TextPrimary,
    surfaceVariant = LiquidDarkSurface,
    onSurfaceVariant = TextSecondary,
    outline = LiquidGlassBorder,
    outlineVariant = LiquidGlassGlow,
    surfaceContainer = LiquidDarkCard,
    surfaceContainerHigh = LiquidDarkCardElevated,
    surfaceContainerHighest = LiquidDarkCardElevated,
    error = NekoPink,
    onError = TextPrimary
)

@Composable
fun KonaNekoTheme(
    darkTheme: Boolean = true, // Enforce liquid dark theme as requested
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = ObsidianVoid.toArgb()
            window.navigationBarColor = ObsidianVoid.toArgb()
            val windowInsetsController = WindowCompat.getInsetsController(window, view)
            windowInsetsController.isAppearanceLightStatusBars = false
            windowInsetsController.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = LiquidDarkColorScheme,
        typography = Typography,
        content = content
    )
}

package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private fun createLightColorScheme(appColors: AppColorScheme) = lightColorScheme(
    primary = appColors.heroAccent,
    onPrimary = appColors.cardBg,
    primaryContainer = appColors.heroBg,
    onPrimaryContainer = appColors.heroText,
    secondary = appColors.heroAccent,
    onSecondary = appColors.cardBg,
    secondaryContainer = appColors.heroBg,
    onSecondaryContainer = appColors.heroText,
    tertiary = appColors.badgePurpleIcon,
    onTertiary = appColors.cardBg,
    background = appColors.canvasBg,
    onBackground = appColors.textPrimary,
    surface = appColors.cardBg,
    onSurface = appColors.textPrimary,
    surfaceVariant = appColors.canvasBg,
    onSurfaceVariant = appColors.textSecondary,
    error = appColors.riskCritical,
    onError = appColors.cardBg,
    outline = appColors.cardBorder
)

private fun createDarkColorScheme(appColors: AppColorScheme) = darkColorScheme(
    primary = appColors.heroAccent,
    onPrimary = appColors.cardBg,
    primaryContainer = appColors.heroBg,
    onPrimaryContainer = appColors.heroText,
    secondary = appColors.heroAccent,
    onSecondary = appColors.cardBg,
    secondaryContainer = appColors.heroBg,
    onSecondaryContainer = appColors.heroText,
    tertiary = appColors.badgePurpleIcon,
    onTertiary = appColors.cardBg,
    background = appColors.canvasBg,
    onBackground = appColors.textPrimary,
    surface = appColors.cardBg,
    onSurface = appColors.textPrimary,
    surfaceVariant = appColors.canvasBg,
    onSurfaceVariant = appColors.textSecondary,
    error = appColors.riskCritical,
    onError = appColors.cardBg,
    outline = appColors.cardBorder
)

@Composable
fun MyApplicationTheme(
    themeMode: AppThemeMode = ThemeManager.currentThemeMode,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    ThemeManager.init(context)

    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> systemDark
    }

    val appColors = if (isDark) DarkAppColorScheme else LightAppColorScheme
    val colorScheme = if (isDark) createDarkColorScheme(appColors) else createLightColorScheme(appColors)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = appColors.canvasBg.toArgb()
            window.navigationBarColor = appColors.cardBg.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDark
        }
    }

    CompositionLocalProvider(
        LocalAppColors provides appColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

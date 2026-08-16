package com.example.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppThemeMode(val title: String, val description: String) {
    LIGHT("White Screen (Light)", "Clean, bright, high-contrast light bento canvas"),
    DARK("Black Screen (Cyber Dark)", "Deep OLED dark canvas with vivid cyber accents"),
    SYSTEM("Follow System", "Automatically matches your Android system display setting")
}

data class AppColorScheme(
    val isDark: Boolean,
    val canvasBg: Color,
    val cardBg: Color,
    val cardBorder: Color,
    val cardBorderSubtle: Color,
    val heroBg: Color,
    val heroBorder: Color,
    val heroText: Color,
    val heroAccent: Color,
    val heroTrack: Color,
    val badgePurple: Color,
    val badgePurpleIcon: Color,
    val badgeBlue: Color,
    val badgeBlueIcon: Color,
    val badgePeach: Color,
    val badgePeachIcon: Color,
    val badgeSlate: Color,
    val badgeSlateIcon: Color,
    val badgeGreen: Color,
    val badgeGreenIcon: Color,
    val alertBg: Color,
    val alertBorder: Color,
    val alertText: Color,
    val alertIconBg: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val riskCritical: Color,
    val riskHigh: Color,
    val riskMedium: Color,
    val riskLow: Color,
    val riskSafe: Color
)

val LightAppColorScheme = AppColorScheme(
    isDark = false,
    canvasBg = Color(0xFFF3F4F9),
    cardBg = Color(0xFFFFFFFF),
    cardBorder = Color(0xFFE1E2EC),
    cardBorderSubtle = Color(0xFFDDE2F1),
    heroBg = Color(0xFFD3E3FD),
    heroBorder = Color(0xFFA8C7FA),
    heroText = Color(0xFF001D35),
    heroAccent = Color(0xFF0B57D0),
    heroTrack = Color(0xFFF0F4FF),
    badgePurple = Color(0xFFE8DEF8),
    badgePurpleIcon = Color(0xFF6750A4),
    badgeBlue = Color(0xFFE0F3FF),
    badgeBlueIcon = Color(0xFF0288D1),
    badgePeach = Color(0xFFF9EEEB),
    badgePeachIcon = Color(0xFFD97706),
    badgeSlate = Color(0xFFF2F0F4),
    badgeSlateIcon = Color(0xFF475569),
    badgeGreen = Color(0xFFE6F4EA),
    badgeGreenIcon = Color(0xFF137333),
    alertBg = Color(0xFFF9DEDC),
    alertBorder = Color(0xFFF2B8B5),
    alertText = Color(0xFF410E0B),
    alertIconBg = Color(0xFFB3261E),
    textPrimary = Color(0xFF1B1B1F),
    textSecondary = Color(0xFF44474E),
    textMuted = Color(0xFF74777F),
    riskCritical = Color(0xFFB3261E),
    riskHigh = Color(0xFFBA1A1A),
    riskMedium = Color(0xFFE26D00),
    riskLow = Color(0xFF1B873F),
    riskSafe = Color(0xFF0B57D0)
)

val DarkAppColorScheme = AppColorScheme(
    isDark = true,
    canvasBg = Color(0xFF0B0E14), // Deep Black Screen Canvas
    cardBg = Color(0xFF141923),   // Elevated Dark Card
    cardBorder = Color(0xFF232C3D),
    cardBorderSubtle = Color(0xFF1E2638),
    heroBg = Color(0xFF152238),
    heroBorder = Color(0xFF2B436E),
    heroText = Color(0xFFE2EDFE),
    heroAccent = Color(0xFF58A6FF),
    heroTrack = Color(0xFF101926),
    badgePurple = Color(0xFF251A38),
    badgePurpleIcon = Color(0xFFC084FC),
    badgeBlue = Color(0xFF12283E),
    badgeBlueIcon = Color(0xFF38BDF8),
    badgePeach = Color(0xFF332014),
    badgePeachIcon = Color(0xFFFBBF24),
    badgeSlate = Color(0xFF1B222D),
    badgeSlateIcon = Color(0xFF94A3B8),
    badgeGreen = Color(0xFF102D1E),
    badgeGreenIcon = Color(0xFF4ADE80),
    alertBg = Color(0xFF3E1917),
    alertBorder = Color(0xFF632320),
    alertText = Color(0xFFFFD8D6),
    alertIconBg = Color(0xFFF87171),
    textPrimary = Color(0xFFF1F5F9), // Crisp Bright White on Dark
    textSecondary = Color(0xFF94A3B8),
    textMuted = Color(0xFF64748B),
    riskCritical = Color(0xFFF87171),
    riskHigh = Color(0xFFFB7185),
    riskMedium = Color(0xFFFBBF24),
    riskLow = Color(0xFF4ADE80),
    riskSafe = Color(0xFF38BDF8)
)

val LocalAppColors = compositionLocalOf { LightAppColorScheme }

object ThemeManager {
    private const val PREFS_NAME = "shield_ai_theme_prefs"
    private const val KEY_THEME_MODE = "key_app_theme_mode"

    private var sharedPreferences: SharedPreferences? = null

    private val _themeModeFlow = MutableStateFlow(AppThemeMode.SYSTEM)
    val themeModeFlow: StateFlow<AppThemeMode> = _themeModeFlow.asStateFlow()

    var currentThemeMode by mutableStateOf(AppThemeMode.SYSTEM)
        private set

    fun init(context: Context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val savedModeName = sharedPreferences?.getString(KEY_THEME_MODE, AppThemeMode.SYSTEM.name) ?: AppThemeMode.SYSTEM.name
            val mode = try {
                AppThemeMode.valueOf(savedModeName)
            } catch (_: Exception) {
                AppThemeMode.SYSTEM
            }
            currentThemeMode = mode
            _themeModeFlow.value = mode
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        currentThemeMode = mode
        _themeModeFlow.value = mode
        sharedPreferences?.edit()?.putString(KEY_THEME_MODE, mode.name)?.apply()
    }

    fun toggleWhiteBlackTheme() {
        val newMode = when (currentThemeMode) {
            AppThemeMode.LIGHT -> AppThemeMode.DARK
            AppThemeMode.DARK -> AppThemeMode.LIGHT
            AppThemeMode.SYSTEM -> AppThemeMode.DARK
        }
        setThemeMode(newMode)
    }
}

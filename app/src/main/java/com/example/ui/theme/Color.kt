package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// Dynamic Bento Grid System Palette (adapts automatically to White Screen & Black Screen)

val BentoCanvasBg: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.canvasBg

val BentoCardBg: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.cardBg

val BentoCardBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.cardBorder

val BentoCardBorderSubtle: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.cardBorderSubtle

// Hero Bento Tile
val BentoHeroBg: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.heroBg

val BentoHeroBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.heroBorder

val BentoHeroText: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.heroText

val BentoHeroAccent: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.heroAccent

val BentoHeroTrack: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.heroTrack

// Pastel Module Icon Badges
val BentoBadgePurple: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.badgePurple

val BentoBadgePurpleIcon: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.badgePurpleIcon

val BentoBadgeBlue: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.badgeBlue

val BentoBadgeBlueIcon: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.badgeBlueIcon

val BentoBadgePeach: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.badgePeach

val BentoBadgePeachIcon: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.badgePeachIcon

val BentoBadgeSlate: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.badgeSlate

val BentoBadgeSlateIcon: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.badgeSlateIcon

val BentoBadgeGreen: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.badgeGreen

val BentoBadgeGreenIcon: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.badgeGreenIcon

// Alert / Critical Issue Bento Banner
val BentoAlertBg: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.alertBg

val BentoAlertBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.alertBorder

val BentoAlertText: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.alertText

val BentoAlertIconBg: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.alertIconBg

// Text Colors
val BentoTextPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.textPrimary

val BentoTextSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.textSecondary

val BentoTextMuted: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.textMuted

// Risk Semantic Colors
val RiskCritical: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.riskCritical

val RiskHigh: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.riskHigh

val RiskMedium: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.riskMedium

val RiskLow: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.riskLow

val RiskSafe: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.riskSafe

// Legacy alias mappings for backward compatibility
val CyberNavyDark: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.canvasBg

val CyberSurfaceDark: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.cardBg

val CyberCardDark: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.cardBg

val CyberCardBorderDark: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.cardBorder

val CyberShieldGreen: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.heroAccent

val CyberShieldGreenDark: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.heroText

val CyberEmeraldLight: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.badgeGreenIcon

val CyberCyan: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.heroAccent

val CyberBlue: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.heroAccent

val CyberPurple: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.badgePurpleIcon

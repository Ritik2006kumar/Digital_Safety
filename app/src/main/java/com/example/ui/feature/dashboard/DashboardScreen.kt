package com.example.ui.feature.dashboard

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VpnLock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.IssueActionType
import com.example.domain.model.RiskLevel
import com.example.domain.model.SecurityIssue
import com.example.ui.components.RiskBadge
import com.example.ui.components.SecurityScoreGauge
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.BentoAlertBg
import com.example.ui.theme.BentoAlertBorder
import com.example.ui.theme.BentoAlertIconBg
import com.example.ui.theme.BentoAlertText
import com.example.ui.theme.BentoBadgeBlue
import com.example.ui.theme.BentoBadgeBlueIcon
import com.example.ui.theme.BentoBadgeGreen
import com.example.ui.theme.BentoBadgeGreenIcon
import com.example.ui.theme.BentoBadgePeach
import com.example.ui.theme.BentoBadgePeachIcon
import com.example.ui.theme.BentoBadgePurple
import com.example.ui.theme.BentoBadgePurpleIcon
import com.example.ui.theme.BentoBadgeSlate
import com.example.ui.theme.BentoBadgeSlateIcon
import com.example.ui.theme.BentoCanvasBg
import com.example.ui.theme.BentoCardBg
import com.example.ui.theme.BentoCardBorder
import com.example.ui.theme.BentoHeroAccent
import com.example.ui.theme.BentoHeroBg
import com.example.ui.theme.BentoHeroBorder
import com.example.ui.theme.BentoHeroText
import com.example.ui.theme.BentoTextMuted
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.RiskCritical
import com.example.ui.theme.ThemeManager
import com.example.ui.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToApps: () -> Unit,
    onNavigateToPermissions: () -> Unit,
    onNavigateToNetwork: () -> Unit,
    onNavigateToScam: () -> Unit,
    onNavigateToAi: () -> Unit,
    onNavigateToVault: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToProSecurity: (initialTab: Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showThemeDialog by remember { mutableStateOf(false) }

    val score = uiState.score?.totalScore ?: 82
    val riskLevel = uiState.score?.riskLevel ?: RiskLevel.LOW
    val issues = uiState.score?.issuesList ?: emptyList()

    val deviceScore = uiState.score?.deviceScore ?: 18
    val appScore = uiState.score?.appScore ?: 16
    val permissionScore = uiState.score?.permissionScore ?: 19
    val networkScore = uiState.score?.networkScore ?: 14

    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentMode = ThemeManager.currentThemeMode,
            onSelectMode = { mode ->
                ThemeManager.setThemeMode(mode)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BentoCanvasBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Bento Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "OVERVIEW",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = BentoTextSecondary
                    )
                    Text(
                        text = "Digital Safety",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Theme Switcher Button (White Screen vs Black Screen)
                    IconButton(
                        onClick = { showThemeDialog = true },
                        modifier = Modifier
                            .testTag("theme_toggle_button")
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(BentoCardBg)
                            .border(1.5.dp, BentoCardBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = when (ThemeManager.currentThemeMode) {
                                AppThemeMode.LIGHT -> Icons.Default.LightMode
                                AppThemeMode.DARK -> Icons.Default.DarkMode
                                AppThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
                            },
                            contentDescription = "Switch Theme (White/Black Screen)",
                            tint = BentoHeroAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { viewModel.rescan() },
                        modifier = Modifier
                            .testTag("rescan_button")
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(BentoCardBg)
                            .border(1.5.dp, BentoCardBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Rescan",
                            tint = BentoTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Scanning Status Banner
        item {
            AnimatedVisibility(visible = uiState.isScanning) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BentoHeroBg),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoHeroBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = BentoHeroAccent,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = uiState.scanStep,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoHeroText
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = BentoHeroAccent,
                            trackColor = Color(0xFFF0F4FF)
                        )
                    }
                }
            }
        }

        // Bento Hero Score Card (Light Sky Blue Container)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BentoHeroBg),
                shape = RoundedCornerShape(32.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoHeroBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SecurityScoreGauge(
                        score = score,
                        riskLevel = riskLevel,
                        isScanning = uiState.isScanning,
                        size = 140.dp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Status Pill
                    Surface(
                        color = BentoHeroAccent,
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Text(
                            text = when {
                                score >= 90 -> "EXCELLENT PROTECTION"
                                score >= 75 -> "GOOD SECURITY"
                                score >= 50 -> "NEEDS ATTENTION"
                                else -> "CRITICAL RISK"
                            },
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Scan CTA Button
                    Button(
                        onClick = { viewModel.rescan() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("run_security_scan_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BentoHeroText,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (uiState.isScanning) "Auditing Posture..." else "Run Security Scan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Real-Time Background Sentinel Bento Card
        item {
            val bgProtection = uiState.backgroundProtection
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                shape = RoundedCornerShape(28.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Header with Master Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (bgProtection.isProtectionEnabled) BentoBadgeGreen else BentoBadgeSlate),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (bgProtection.isProtectionEnabled) Icons.Default.HealthAndSafety else Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = if (bgProtection.isProtectionEnabled) BentoBadgeGreenIcon else BentoBadgeSlateIcon,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Background Sentinel",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BentoTextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = if (bgProtection.isProtectionEnabled) BentoBadgeGreenIcon.copy(alpha = 0.12f) else BentoTextMuted.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(100.dp)
                                    ) {
                                        Text(
                                            text = if (bgProtection.isProtectionEnabled) "LIVE" else "PAUSED",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (bgProtection.isProtectionEnabled) BentoBadgeGreenIcon else BentoTextMuted,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (bgProtection.isProtectionEnabled) "Real-time background security engine" else "Background monitoring paused",
                                    fontSize = 11.sp,
                                    color = BentoTextSecondary
                                )
                            }
                        }

                        Switch(
                            checked = bgProtection.isProtectionEnabled,
                            onCheckedChange = { viewModel.toggleBackgroundProtection(it) },
                            modifier = Modifier.testTag("bg_protection_switch"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = BentoBadgeGreenIcon,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = BentoCardBorder
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Live Sentinel Guard Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.toggleAppWatchdog(!bgProtection.isAppWatchdogActive) },
                            color = if (bgProtection.isAppWatchdogActive) BentoBadgeBlue.copy(alpha = 0.6f) else BentoCanvasBg,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
                        ) {
                            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Apps,
                                    contentDescription = null,
                                    tint = if (bgProtection.isAppWatchdogActive) BentoBadgeBlueIcon else BentoTextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "App Watchdog",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (bgProtection.isAppWatchdogActive) BentoTextPrimary else BentoTextMuted
                                )
                                Text(
                                    text = if (bgProtection.isAppWatchdogActive) "Active" else "Off",
                                    fontSize = 9.sp,
                                    color = if (bgProtection.isAppWatchdogActive) BentoBadgeBlueIcon else BentoTextMuted
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.toggleNetworkGuard(!bgProtection.isNetworkGuardActive) },
                            color = if (bgProtection.isNetworkGuardActive) BentoBadgeSlate.copy(alpha = 0.6f) else BentoCanvasBg,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
                        ) {
                            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Wifi,
                                    contentDescription = null,
                                    tint = if (bgProtection.isNetworkGuardActive) BentoBadgeSlateIcon else BentoTextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Wi-Fi Guard",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (bgProtection.isNetworkGuardActive) BentoTextPrimary else BentoTextMuted
                                )
                                Text(
                                    text = if (bgProtection.isNetworkGuardActive) "Active" else "Off",
                                    fontSize = 9.sp,
                                    color = if (bgProtection.isNetworkGuardActive) BentoBadgeSlateIcon else BentoTextMuted
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.toggleClipboardShield(!bgProtection.isClipboardScamShieldActive) },
                            color = if (bgProtection.isClipboardScamShieldActive) BentoBadgePeach.copy(alpha = 0.6f) else BentoCanvasBg,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
                        ) {
                            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.VpnLock,
                                    contentDescription = null,
                                    tint = if (bgProtection.isClipboardScamShieldActive) BentoBadgePeachIcon else BentoTextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Phishing Guard",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (bgProtection.isClipboardScamShieldActive) BentoTextPrimary else BentoTextMuted
                                )
                                Text(
                                    text = if (bgProtection.isClipboardScamShieldActive) "Active" else "Off",
                                    fontSize = 9.sp,
                                    color = if (bgProtection.isClipboardScamShieldActive) BentoBadgePeachIcon else BentoTextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Battery Saver Optimization Notice & Action
                    Surface(
                        color = BentoCanvasBg,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = Icons.Default.BatteryChargingFull,
                                    contentDescription = null,
                                    tint = BentoHeroAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Android Battery Optimization",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BentoTextPrimary
                                    )
                                    Text(
                                        text = "Allow background activity so Android won't sleep the security guard",
                                        fontSize = 10.sp,
                                        color = BentoTextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Button(
                                onClick = { viewModel.openBatterySettings() },
                                colors = ButtonDefaults.buttonColors(containerColor = BentoCardBg, contentColor = BentoHeroText),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Settings", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Run Live Background Diagnostic
                    Button(
                        onClick = { viewModel.runBackgroundDiagnostic() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .testTag("run_bg_diagnostics_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BentoHeroBg,
                            contentColor = BentoHeroText
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState.isDiagnosticsRunning) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = BentoHeroText,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Testing Background Triggers...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(imageVector = Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Run Live Background Diagnostic", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 2x2 Bento Stat Tiles (Device, Apps, Privacy, Network)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BentoStatCard(
                        scoreText = "$deviceScore/20",
                        label = "DEVICE",
                        icon = Icons.Default.PhoneAndroid,
                        iconBg = BentoBadgePurple,
                        iconTint = BentoBadgePurpleIcon,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToReports
                    )
                    BentoStatCard(
                        scoreText = "$appScore/20",
                        label = "APPS",
                        icon = Icons.Default.Apps,
                        iconBg = BentoBadgeBlue,
                        iconTint = BentoBadgeBlueIcon,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToApps
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BentoStatCard(
                        scoreText = "$permissionScore/20",
                        label = "PRIVACY",
                        icon = Icons.Default.Policy,
                        iconBg = BentoBadgePeach,
                        iconTint = BentoBadgePeachIcon,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToPermissions
                    )
                    BentoStatCard(
                        scoreText = "$networkScore/20",
                        label = "NETWORK",
                        icon = Icons.Default.Wifi,
                        iconBg = BentoBadgeSlate,
                        iconTint = BentoBadgeSlateIcon,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToNetwork
                    )
                }
            }
        }

        // Critical / Active Issues Alert Bento Card
        if (issues.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BentoAlertBg),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoAlertBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(BentoAlertIconBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${issues.size} Critical Issues",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoAlertText
                                )
                                Text(
                                    text = issues.firstOrNull()?.title ?: "Network encryption & app permissions",
                                    fontSize = 11.sp,
                                    color = BentoAlertText.copy(alpha = 0.8f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                issues.firstOrNull()?.let { handleIssueAction(context, it) }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BentoAlertText,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(text = "FIX", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Detailed Issue List
            items(issues) { issue ->
                BentoIssueCard(
                    issue = issue,
                    onFixClicked = { handleIssueAction(context, issue) }
                )
            }
        } else {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BentoBadgeGreen.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(BentoBadgeGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = BentoBadgeGreenIcon,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Zero Critical Vulnerabilities",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoTextPrimary
                            )
                            Text(
                                text = "Device lock active, network encrypted, privacy intact.",
                                fontSize = 11.sp,
                                color = BentoTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Security Modules Bento Section
        item {
            Text(
                text = "SECURITY MODULES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = BentoTextSecondary
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BentoModuleCard(
                    title = "App Auditor",
                    subtitle = "${uiState.scannedApps.size} Apps Scanned",
                    icon = Icons.Default.Apps,
                    badgeBg = BentoBadgeBlue,
                    badgeTint = BentoBadgeBlueIcon,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToApps
                )
                BentoModuleCard(
                    title = "Permission Matrix",
                    subtitle = "Privacy Privileges",
                    icon = Icons.Default.Policy,
                    badgeBg = BentoBadgePurple,
                    badgeTint = BentoBadgePurpleIcon,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToPermissions
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BentoModuleCard(
                    title = "Network Diagnostics",
                    subtitle = uiState.networkInfo?.connectionType ?: "Wi-Fi Guard",
                    icon = Icons.Default.Wifi,
                    badgeBg = BentoBadgeSlate,
                    badgeTint = BentoBadgeSlateIcon,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToNetwork
                )
                BentoModuleCard(
                    title = "Scam & URL Shield",
                    subtitle = "Phishing Filter",
                    icon = Icons.Default.VpnLock,
                    badgeBg = BentoBadgePeach,
                    badgeTint = BentoBadgePeachIcon,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToScam
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BentoModuleCard(
                    title = "Encrypted Vault",
                    subtitle = "AES-256 Lock",
                    icon = Icons.Default.Key,
                    badgeBg = BentoBadgeGreen,
                    badgeTint = BentoBadgeGreenIcon,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToVault
                )
                BentoModuleCard(
                    title = "ShieldAI Advisor",
                    subtitle = "AI Security Chat",
                    icon = Icons.Default.AutoAwesome,
                    badgeBg = BentoHeroBg,
                    badgeTint = BentoHeroAccent,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToAi
                )
            }
        }

        // PRO CYBERSECURITY SUITE
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "PRO SECURITY SUITE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = BentoTextSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(BentoHeroAccent)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "PRO MAX",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Pro Suite 2x2 Bento Cards
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BentoModuleCard(
                    title = "Spyware & RAT Scanner",
                    subtitle = "Heuristic Detection",
                    icon = Icons.Default.BugReport,
                    badgeBg = BentoAlertBg,
                    badgeTint = RiskCritical,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToProSecurity(1) }
                )
                BentoModuleCard(
                    title = "Emergency Lockdown",
                    subtitle = "One-Tap Panic Button",
                    icon = Icons.Default.Dangerous,
                    badgeBg = BentoAlertBg,
                    badgeTint = RiskCritical,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToProSecurity(0) }
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BentoModuleCard(
                    title = "App Privacy Trackers",
                    subtitle = "Telemetry SDK Audit",
                    icon = Icons.Default.Radar,
                    badgeBg = BentoBadgePurple,
                    badgeTint = BentoBadgePurpleIcon,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToProSecurity(2) }
                )
                BentoModuleCard(
                    title = "Dark Web Breaches",
                    subtitle = "8B+ Leaked Accounts",
                    icon = Icons.Default.Public,
                    badgeBg = BentoBadgeSlate,
                    badgeTint = BentoBadgeSlateIcon,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToProSecurity(3) }
                )
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToReports() },
                colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(BentoBadgeBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = BentoBadgeBlueIcon,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Security Audit Reports",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoTextPrimary
                            )
                            Text(
                                text = "View history trends & export audit summary",
                                fontSize = 11.sp,
                                color = BentoTextSecondary
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = BentoTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BentoStatCard(
    scoreText: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .height(115.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = BentoCardBg),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Text(
                    text = scoreText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary
                )
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextSecondary
                )
            }
        }
    }
}

@Composable
fun BentoModuleCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badgeBg: Color,
    badgeTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = BentoCardBg),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(badgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = badgeTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = BentoTextMuted.copy(alpha = 0.6f),
                    modifier = Modifier.size(14.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = BentoTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun BentoIssueCard(
    issue: SecurityIssue,
    onFixClicked: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BentoCardBg),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = issue.category.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextSecondary
                )
                RiskBadge(riskLevel = issue.riskLevel)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = issue.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = BentoTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = issue.description,
                fontSize = 12.sp,
                color = BentoTextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onFixClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .testTag("fix_issue_${issue.id}"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BentoHeroBg,
                    contentColor = BentoHeroText
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = issue.actionLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(12.dp))
            }
        }
    }
}

private fun handleIssueAction(context: Context, issue: SecurityIssue) {
    try {
        when (issue.actionType) {
            IssueActionType.OPEN_APP_SETTINGS -> {
                val pkg = issue.targetPackage ?: context.packageName
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", pkg, null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
            IssueActionType.OPEN_DEVICE_SECURITY_SETTINGS -> {
                val intent = Intent(Settings.ACTION_SECURITY_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
            IssueActionType.OPEN_DEVELOPER_SETTINGS -> {
                val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
            IssueActionType.OPEN_WIFI_SETTINGS -> {
                val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
            else -> {}
        }
    } catch (_: Exception) {
        val intent = Intent(Settings.ACTION_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}

@Composable
fun ThemeSelectionDialog(
    currentMode: AppThemeMode,
    onSelectMode: (AppThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DarkMode,
                    contentDescription = null,
                    tint = BentoHeroAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Display Theme / Screen Mode",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AppThemeMode.entries.forEach { mode ->
                    val isSelected = currentMode == mode
                    Card(
                        onClick = { onSelectMode(mode) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("theme_option_${mode.name.lowercase()}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) BentoHeroBg else BentoCardBg
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) BentoHeroAccent else BentoCardBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (mode) {
                                                AppThemeMode.LIGHT -> Color(0xFFF3F4F9)
                                                AppThemeMode.DARK -> Color(0xFF1E2530)
                                                AppThemeMode.SYSTEM -> BentoHeroBg
                                            }
                                        )
                                        .border(
                                            1.dp,
                                            if (mode == AppThemeMode.LIGHT) Color(0xFFCBD5E1) else Color(0xFF475569),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (mode) {
                                            AppThemeMode.LIGHT -> Icons.Default.LightMode
                                            AppThemeMode.DARK -> Icons.Default.DarkMode
                                            AppThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
                                        },
                                        contentDescription = null,
                                        tint = when (mode) {
                                            AppThemeMode.LIGHT -> Color(0xFFD97706)
                                            AppThemeMode.DARK -> Color(0xFF38BDF8)
                                            AppThemeMode.SYSTEM -> BentoHeroAccent
                                        },
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = mode.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) BentoHeroText else BentoTextPrimary
                                    )
                                    Text(
                                        text = mode.description,
                                        fontSize = 11.sp,
                                        color = BentoTextSecondary,
                                        lineHeight = 14.sp
                                    )
                                }
                            }

                            RadioButton(
                                selected = isSelected,
                                onClick = { onSelectMode(mode) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = BentoHeroAccent,
                                    unselectedColor = BentoTextMuted
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("close_theme_dialog")
            ) {
                Text("Done", fontWeight = FontWeight.Bold, color = BentoHeroAccent)
            }
        },
        containerColor = BentoCardBg,
        shape = RoundedCornerShape(24.dp)
    )
}



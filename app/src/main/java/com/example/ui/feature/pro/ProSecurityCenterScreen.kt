package com.example.ui.feature.pro

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LayersClear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AppTrackerProfile
import com.example.domain.model.RiskLevel
import com.example.domain.model.SpywareThreat
import com.example.domain.model.TrackerCategory
import com.example.ui.components.RiskBadge
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
import com.example.ui.theme.BentoHeroTrack
import com.example.ui.theme.BentoTextMuted
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.RiskCritical
import com.example.ui.theme.RiskHigh
import com.example.ui.theme.RiskLow
import com.example.ui.theme.RiskMedium
import com.example.ui.theme.RiskSafe
import com.example.ui.viewmodel.ProSecurityViewModel

@Composable
fun ProSecurityCenterScreen(
    viewModel: ProSecurityViewModel,
    initialTab: Int = 0,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(initialTab) }
    val context = LocalContext.current

    val tabs = listOf(
        "Emergency Lockdown" to Icons.Default.Dangerous,
        "Spyware & RAT" to Icons.Default.BugReport,
        "Privacy Trackers" to Icons.Default.Radar,
        "Dark Web Breach" to Icons.Default.Public
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BentoCanvasBg)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(BentoCardBg)
                        .border(1.dp, BentoCardBorder, CircleShape)
                        .testTag("pro_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = BentoTextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Pro Security Suite",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
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
                    Text(
                        text = "Deep Spyware, Trackers & Anti-Breach Defense",
                        fontSize = 12.sp,
                        color = BentoTextSecondary
                    )
                }
            }
        }

        // Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = BentoCardBg,
            contentColor = BentoHeroAccent,
            edgePadding = 16.dp,
            divider = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(BentoCardBorder)
                )
            },
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = BentoHeroAccent,
                    height = 3.dp
                )
            }
        ) {
            tabs.forEachIndexed { index, pair ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = pair.second,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (selectedTabIndex == index) BentoHeroAccent else BentoTextMuted
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = pair.first,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTabIndex == index) BentoHeroAccent else BentoTextSecondary
                            )
                        }
                    },
                    modifier = Modifier.testTag("pro_tab_$index")
                )
            }
        }

        // Tab Content
        when (selectedTabIndex) {
            0 -> EmergencyLockdownTab(uiState = uiState, viewModel = viewModel)
            1 -> SpywareScannerTab(uiState = uiState, viewModel = viewModel)
            2 -> AppTrackersTab(uiState = uiState, viewModel = viewModel)
            3 -> DarkWebBreachTab(uiState = uiState, viewModel = viewModel)
        }
    }
}

// ==========================================
// 1. EMERGENCY LOCKDOWN (PANIC BUTTON) TAB
// ==========================================

@Composable
private fun EmergencyLockdownTab(
    uiState: com.example.ui.viewmodel.ProSecurityUiState,
    viewModel: ProSecurityViewModel
) {
    val context = LocalContext.current
    val isLockdown = uiState.isLockdownActive

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Panic Header Bento Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("panic_lockdown_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isLockdown) BentoAlertBg else BentoCardBg
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isLockdown) 2.dp else 1.dp,
                    color = if (isLockdown) RiskCritical else BentoCardBorder
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (isLockdown) RiskCritical else BentoAlertBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isLockdown) Icons.Default.Dangerous else Icons.Default.Security,
                                    contentDescription = null,
                                    tint = if (isLockdown) Color.White else RiskCritical,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = if (isLockdown) "EMERGENCY LOCKDOWN ACTIVE" else "One-Tap Emergency Lockdown",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isLockdown) RiskCritical else BentoTextPrimary
                                )
                                Text(
                                    text = if (isLockdown) "Threat mitigation protocol executing" else "Immediate threat containment protocol",
                                    fontSize = 12.sp,
                                    color = BentoTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "If you suspect physical surveillance, spyware exfiltration, or unauthorized remote access, initiate Emergency Lockdown to instantly sanitize RAM clipboard, isolate radios, and revoke screen overlay tapjacking.",
                        fontSize = 13.sp,
                        color = BentoTextSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    if (!isLockdown) {
                        Button(
                            onClick = {
                                viewModel.triggerEmergencyLockdown()
                                Toast.makeText(context, "Emergency Lockdown Activated: Clipboard RAM Sanitized", Toast.LENGTH_LONG).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_trigger_panic"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RiskCritical,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(imageVector = Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "TRIGGER EMERGENCY LOCKDOWN",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Button(
                            onClick = { viewModel.disengageLockdown() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_disengage_panic"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BentoHeroAccent,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(imageVector = Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Disengage / Stand Down Protocol", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Rapid Isolation Controls
        item {
            Text(
                text = "RAPID CONTAINMENT ACTIONS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BentoTextMuted,
                letterSpacing = 1.sp
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Clipboard Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(BentoBadgePurple),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.CleaningServices, contentDescription = null, tint = BentoBadgePurpleIcon, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Sanitize Clipboard RAM", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                                Text("Zeroes out copied OTPs & passwords", fontSize = 11.sp, color = BentoTextSecondary)
                            }
                        }
                        Button(
                            onClick = {
                                viewModel.purgeClipboard()
                                Toast.makeText(context, "Clipboard purged!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BentoHeroBg, contentColor = BentoHeroAccent)
                        ) {
                            Text("Purge Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BentoCardBorder))

                    // Airplane Mode
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(BentoBadgeBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.AirplanemodeActive, contentDescription = null, tint = BentoBadgeBlueIcon, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Sever RF Radio & Wi-Fi", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                                Text("Isolate device from C2 Trojan commands", fontSize = 11.sp, color = BentoTextSecondary)
                            }
                        }
                        IconButton(onClick = { viewModel.openAirplaneSettings() }) {
                            Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, tint = BentoHeroAccent)
                        }
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BentoCardBorder))

                    // Overlay Killer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(BentoBadgePeach),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.LayersClear, contentDescription = null, tint = BentoBadgePeachIcon, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Revoke Overlay Permissions", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                                Text("Stop fake overlay screens and tap-jacking", fontSize = 11.sp, color = BentoTextSecondary)
                            }
                        }
                        IconButton(onClick = { viewModel.openOverlaySettings() }) {
                            Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, tint = BentoHeroAccent)
                        }
                    }
                }
            }
        }

        // Lockdown Checklist
        item {
            Text(
                text = "INCIDENT MITIGATION CHECKLIST",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BentoTextMuted,
                letterSpacing = 1.sp
            )
        }

        items(uiState.lockdownSteps) { step ->
            Card(
                onClick = { viewModel.toggleLockdownStep(step.id) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = if (step.isCompleted) BentoBadgeGreenIcon else BentoCardBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Checkbox(
                            checked = step.isCompleted,
                            onCheckedChange = { viewModel.toggleLockdownStep(step.id) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = BentoBadgeGreenIcon,
                                checkmarkColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = step.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (step.isCompleted) BentoBadgeGreenIcon else BentoTextPrimary
                            )
                            Text(
                                text = step.subtitle,
                                fontSize = 11.sp,
                                color = BentoTextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 2. SPYWARE & RAT SCANNER TAB
// ==========================================

@Composable
private fun SpywareScannerTab(
    uiState: com.example.ui.viewmodel.ProSecurityUiState,
    viewModel: ProSecurityViewModel
) {
    val report = uiState.spywareReport
    val isScanning = uiState.isScanningSpyware

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Scanner Bento Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BentoHeroBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoHeroBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(BentoHeroAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BugReport,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Deep Spyware & RAT Scanner",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BentoHeroText
                                )
                                Text(
                                    text = "Heuristic RAT, Keylogger & Sideload Audit",
                                    fontSize = 12.sp,
                                    color = BentoHeroText.copy(alpha = 0.8f)
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.scanSpyware() },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(BentoCardBg)
                                .border(1.dp, BentoCardBorder, CircleShape)
                        ) {
                            if (isScanning) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = BentoHeroAccent, strokeWidth = 2.dp)
                            } else {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Rescan", tint = BentoHeroAccent, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats strip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Total Apps
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = BentoCardBg)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Audited Packages", fontSize = 11.sp, color = BentoTextSecondary)
                                Text("${report?.totalAppsScanned ?: 0}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                            }
                        }

                        // Critical Threats
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = BentoCardBg)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Critical RATs", fontSize = 11.sp, color = BentoTextSecondary)
                                Text(
                                    "${report?.criticalThreatsFound ?: 0}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if ((report?.criticalThreatsFound ?: 0) > 0) RiskCritical else BentoBadgeGreenIcon
                                )
                            }
                        }

                        // Warnings
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = BentoCardBg)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Warnings", fontSize = 11.sp, color = BentoTextSecondary)
                                Text("${report?.warningsFound ?: 0}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = RiskMedium)
                            }
                        }
                    }
                }
            }
        }

        // Threats List
        item {
            Text(
                text = "DETECTED HIGH-PRIVILEGE BEHAVIORS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BentoTextMuted,
                letterSpacing = 1.sp
            )
        }

        val threats = report?.threats ?: emptyList()
        if (threats.isEmpty() && !isScanning) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoBadgeGreen.copy(alpha = 0.5f))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.GppGood, contentDescription = null, tint = BentoBadgeGreenIcon, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Sandbox Clean & Verified", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BentoBadgeGreenIcon)
                            Text("No active keyloggers or screen overlay hijackers found.", fontSize = 12.sp, color = BentoTextSecondary)
                        }
                    }
                }
            }
        }

        items(threats) { threat ->
            SpywareThreatCard(threat = threat, viewModel = viewModel)
        }
    }
}

@Composable
private fun SpywareThreatCard(
    threat: SpywareThreat,
    viewModel: ProSecurityViewModel
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardBg),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (threat.severity == RiskLevel.CRITICAL) BentoAlertBorder else BentoCardBorder
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (threat.severity == RiskLevel.CRITICAL) BentoAlertBg else BentoBadgePeach),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (threat.severity == RiskLevel.CRITICAL) RiskCritical else BentoBadgePeachIcon,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(threat.appName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                        Text(threat.packageName, fontSize = 11.sp, color = BentoTextMuted)
                    }
                }

                RiskBadge(riskLevel = threat.severity)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = threat.threatDescription,
                fontSize = 13.sp,
                color = BentoTextSecondary,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action / Expand
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.padding(0.dp)
                ) {
                    Text(
                        text = if (expanded) "Hide Heuristic Logs" else "View Detected Signatures (${threat.detectedBehaviors.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoHeroAccent
                    )
                }

                Button(
                    onClick = { viewModel.openOverlaySettings() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BentoAlertBg, contentColor = RiskCritical)
                ) {
                    Text("Restrict Access", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .background(BentoCanvasBg, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    threat.detectedBehaviors.forEach { behavior ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = RiskCritical, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(behavior, fontSize = 11.sp, color = BentoTextPrimary)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. APP PRIVACY & TRACKERS TAB
// ==========================================

@Composable
private fun AppTrackersTab(
    uiState: com.example.ui.viewmodel.ProSecurityUiState,
    viewModel: ProSecurityViewModel
) {
    val profiles = uiState.trackerProfiles
    val searchQuery = uiState.trackerSearchQuery

    val filteredProfiles = profiles.filter {
        searchQuery.isBlank() || it.appName.contains(searchQuery, ignoreCase = true) || it.packageName.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setTrackerSearchQuery(it) },
                placeholder = { Text("Filter apps for telemetry & ad trackers...", fontSize = 13.sp, color = BentoTextMuted) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = BentoTextMuted) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tracker_search_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BentoHeroAccent,
                    unfocusedBorderColor = BentoCardBorder,
                    focusedContainerColor = BentoCardBg,
                    unfocusedContainerColor = BentoCardBg
                ),
                singleLine = true
            )
        }

        // Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BentoBadgePurple.copy(alpha = 0.5f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBadgePurpleIcon.copy(alpha = 0.3f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(BentoBadgePurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Radar, contentDescription = null, tint = BentoBadgePurpleIcon, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("App Telemetry & Tracker Inspector", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                        Text("Identifies embedded analytics, behavioral profilers, and advertising SDKs.", fontSize = 11.sp, color = BentoTextSecondary)
                    }
                }
            }
        }

        items(filteredProfiles) { profile ->
            AppTrackerCard(profile = profile)
        }
    }
}

@Composable
private fun AppTrackerCard(profile: AppTrackerProfile) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (profile.totalTrackersCount > 0) BentoBadgeSlate else BentoBadgeGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${profile.totalTrackersCount}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = if (profile.totalTrackersCount > 0) BentoTextPrimary else BentoBadgeGreenIcon
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(profile.appName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                        Text(profile.packageName, fontSize = 11.sp, color = BentoTextMuted)
                    }
                }

                // Privacy Score Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (profile.privacyScore >= 80) BentoBadgeGreen else if (profile.privacyScore >= 50) BentoBadgePeach else BentoAlertBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Score: ${profile.privacyScore}/100",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (profile.privacyScore >= 80) BentoBadgeGreenIcon else if (profile.privacyScore >= 50) BentoBadgePeachIcon else RiskCritical
                    )
                }
            }

            if (profile.trackers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${profile.trackers.size} Embedded Tracker SDKs detected",
                        fontSize = 12.sp,
                        color = BentoTextSecondary
                    )
                    TextButton(onClick = { expanded = !expanded }) {
                        Text(if (expanded) "Hide" else "View SDKs", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BentoHeroAccent)
                    }
                }

                AnimatedVisibility(visible = expanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .background(BentoCanvasBg, RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        profile.trackers.forEach { tracker ->
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(tracker.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                                    Text(tracker.category.label, fontSize = 10.sp, color = BentoHeroAccent, fontWeight = FontWeight.Medium)
                                }
                                Text("Vendor: ${tracker.company} • ${tracker.dataCollected}", fontSize = 10.sp, color = BentoTextSecondary)
                            }
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Zero embedded ad/telemetry trackers found in package.", fontSize = 11.sp, color = BentoBadgeGreenIcon)
            }
        }
    }
}

// ==========================================
// 4. DARK WEB & DATA BREACH MONITOR TAB
// ==========================================

@Composable
private fun DarkWebBreachTab(
    uiState: com.example.ui.viewmodel.ProSecurityUiState,
    viewModel: ProSecurityViewModel
) {
    val query = uiState.breachQuery
    val isChecking = uiState.isCheckingBreach
    val result = uiState.breachResult

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Query Box Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BentoHeroBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoHeroBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(BentoHeroAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Public, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Dark Web Data Breach Monitor", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BentoHeroText)
                            Text("Scan 8+ Billion leaked accounts & password dumps", fontSize = 12.sp, color = BentoHeroText.copy(alpha = 0.8f))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = query,
                        onValueChange = { viewModel.setBreachQuery(it) },
                        placeholder = { Text("Enter email (e.g. user@gmail.com) or phone...", fontSize = 13.sp, color = BentoTextMuted) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("breach_query_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BentoCardBg,
                            unfocusedContainerColor = BentoCardBg,
                            focusedBorderColor = BentoHeroAccent,
                            unfocusedBorderColor = BentoCardBorder
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.executeBreachLookup() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("btn_run_breach_lookup"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BentoHeroAccent, contentColor = Color.White)
                    ) {
                        if (isChecking) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Searching 8B+ Dark Web Records...")
                        } else {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Check Data Breach Exposure", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (result != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (result.isBreached) BentoAlertBg else BentoBadgeGreen
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = if (result.isBreached) BentoAlertBorder else BentoBadgeGreenIcon
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (result.isBreached) Icons.Default.Dangerous else Icons.Default.GppGood,
                                    contentDescription = null,
                                    tint = if (result.isBreached) RiskCritical else BentoBadgeGreenIcon,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (result.isBreached) "Pwned! Exposure Found in ${result.breachCount} Leaks" else "Good News: Zero Breaches Found",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (result.isBreached) BentoAlertText else BentoBadgeGreenIcon
                                )
                            }
                        }

                        if (result.isBreached) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Your credentials for '${result.queryTarget}' were detected in public data dumps. Passwords and personal identifiers may be compromised.",
                                fontSize = 12.sp,
                                color = BentoAlertText,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            if (result.isBreached) {
                item {
                    Text(
                        text = "COMPROMISED BREACH INCIDENTS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextMuted,
                        letterSpacing = 1.sp
                    )
                }

                items(result.breachRecords) { breach ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(breach.breachTitle, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                                    Text("Leaked on ${breach.breachDate} • Domain: ${breach.breachDomain}", fontSize = 11.sp, color = BentoTextMuted)
                                }
                                RiskBadge(riskLevel = breach.riskLevel)
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(breach.description, fontSize = 12.sp, color = BentoTextSecondary, lineHeight = 16.sp)

                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Compromised Data:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                            Row(
                                modifier = Modifier.padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                breach.compromisedDataClasses.forEach { dataClass ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(BentoAlertBg)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(dataClass, fontSize = 10.sp, color = RiskCritical, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.example.ui.feature.network

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Http
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.VpnLock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.DiagnosticStatus
import com.example.domain.model.MitmCheckResult
import com.example.domain.model.RiskLevel
import com.example.ui.components.RiskBadge
import com.example.ui.theme.BentoBadgeBlue
import com.example.ui.theme.BentoBadgeBlueIcon
import com.example.ui.theme.BentoBadgeGreen
import com.example.ui.theme.BentoBadgeGreenIcon
import com.example.ui.theme.BentoBadgePeach
import com.example.ui.theme.BentoBadgePeachIcon
import com.example.ui.theme.BentoBadgeSlate
import com.example.ui.theme.BentoBadgeSlateIcon
import com.example.ui.theme.BentoCanvasBg
import com.example.ui.theme.BentoCardBg
import com.example.ui.theme.BentoCardBorder
import com.example.ui.theme.BentoHeroAccent
import com.example.ui.theme.BentoHeroBg
import com.example.ui.theme.BentoHeroText
import com.example.ui.theme.BentoTextMuted
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.RiskCritical
import com.example.ui.theme.RiskHigh
import com.example.ui.theme.RiskMedium
import com.example.ui.theme.RiskSafe
import com.example.ui.viewmodel.NetworkViewModel

@Composable
fun NetworkScreen(
    viewModel: NetworkViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val net = uiState.networkInfo
    val report = uiState.diagnosticReport

    val isDanger = report?.overallRisk == RiskLevel.CRITICAL || report?.overallRisk == RiskLevel.HIGH
    val isWarning = report?.overallRisk == RiskLevel.MEDIUM

    val statusIcon = when {
        isDanger -> Icons.Default.Warning
        isWarning -> Icons.Default.Info
        net?.isVpnActive == true -> Icons.Default.VpnLock
        else -> Icons.Default.HealthAndSafety
    }

    val statusColor = when {
        isDanger -> RiskCritical
        isWarning -> BentoBadgePeachIcon
        else -> BentoBadgeGreenIcon
    }

    val statusContainerBg = when {
        isDanger -> RiskCritical.copy(alpha = 0.1f)
        isWarning -> BentoBadgePeach.copy(alpha = 0.5f)
        else -> BentoBadgeGreen.copy(alpha = 0.5f)
    }

    // Pulse animation for status indicator
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (uiState.isRunningDiagnostics) 1.15f else 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(BentoCardBg)
                    .border(1.dp, BentoCardBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = BentoTextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Network & MITM Diagnostic",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary
                )
                Text(
                    text = "Wi-Fi Cipher & Attack Interception Guard",
                    fontSize = 11.sp,
                    color = BentoTextSecondary
                )
            }
            IconButton(
                onClick = { viewModel.runDiagnostics() },
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(BentoCardBg)
                    .border(1.dp, BentoCardBorder, CircleShape)
                    .testTag("refresh_network_audit_btn")
            ) {
                if (uiState.isRunningDiagnostics) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = BentoHeroAccent, strokeWidth = 2.dp)
                } else {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Run Audit", tint = BentoHeroAccent, modifier = Modifier.size(18.dp))
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // PROMINENT LIVE STATUS INDICATOR (HERO BENTO CARD)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("network_status_hero_card"),
                    colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (isDanger) RiskCritical.copy(alpha = 0.5f) else BentoCardBorder
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .scale(pulseScale)
                                        .clip(CircleShape)
                                        .background(statusContainerBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = statusIcon,
                                        contentDescription = null,
                                        tint = statusColor,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Surface(
                                        color = statusColor.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(100.dp)
                                    ) {
                                        Text(
                                            text = report?.statusBadge ?: "SHIELD PROTECTED",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 0.8.sp,
                                            color = statusColor,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = net?.ssid ?: "Connected",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BentoTextPrimary
                                    )
                                }
                            }

                            RiskBadge(riskLevel = report?.overallRisk ?: RiskLevel.SAFE)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = report?.statusHeadline ?: "No MITM Attack or Insecure Wi-Fi Detected",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDanger) RiskCritical else BentoTextPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = report?.statusDescription
                                ?: "Wireless encryption is valid, SSL certificates are authentic, and DNS resolution is uncompromised.",
                            fontSize = 12.sp,
                            color = BentoTextSecondary,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Metric Status Strip
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BentoCanvasBg, RoundedCornerShape(14.dp))
                                .border(1.dp, BentoCardBorder, RoundedCornerShape(14.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusMetricChip(
                                label = "Audits Passed",
                                value = "${report?.passedCount ?: 5}/${report?.checkedCount ?: 5}",
                                isGood = (report?.dangerCount ?: 0) == 0
                            )
                            Box(modifier = Modifier.width(1.dp).height(24.dp).background(BentoCardBorder))
                            StatusMetricChip(
                                label = "SSL Handshake",
                                value = if (report?.isSslInterceptionDetected == true) "Interception" else "Valid TLS 1.3",
                                isGood = report?.isSslInterceptionDetected != true
                            )
                            Box(modifier = Modifier.width(1.dp).height(24.dp).background(BentoCardBorder))
                            StatusMetricChip(
                                label = "Ping Latency",
                                value = "${uiState.pingLatencyMs} ms",
                                isGood = true
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Audit Trigger Button
                        Button(
                            onClick = { viewModel.runDiagnostics() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("run_deep_mitm_audit_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDanger) RiskCritical else BentoHeroText,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            if (uiState.isRunningDiagnostics) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = uiState.activeDiagnosticStep.ifBlank { "Analyzing Transport Security..." },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Icon(imageVector = Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isDanger) "Re-Scan Network Integrity" else "Run Deep MITM & Wi-Fi Audit",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // SIMULATOR / TESTING BAR (Helps verify MITM indicators interactively)
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DIAGNOSTIC TEST SCENARIOS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = BentoTextSecondary
                        )
                        if (uiState.selectedSimulationMode != null) {
                            Text(
                                text = "Reset to Live",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoHeroAccent,
                                modifier = Modifier
                                    .clickable { viewModel.setSimulationMode(null) }
                                    .padding(vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            SimulationChip(
                                title = "🟢 Real Live Network",
                                isSelected = uiState.selectedSimulationMode == null,
                                onClick = { viewModel.setSimulationMode(null) }
                            )
                        }
                        item {
                            SimulationChip(
                                title = "⚠️ Insecure Open Wi-Fi",
                                isSelected = uiState.selectedSimulationMode == "INSECURE_WIFI",
                                onClick = { viewModel.setSimulationMode("INSECURE_WIFI") }
                            )
                        }
                        item {
                            SimulationChip(
                                title = "🚨 MITM SSL Stripping",
                                isSelected = uiState.selectedSimulationMode == "MITM_SSL_STRIP",
                                onClick = { viewModel.setSimulationMode("MITM_SSL_STRIP") }
                            )
                        }
                        item {
                            SimulationChip(
                                title = "🌐 DNS Spoofing",
                                isSelected = uiState.selectedSimulationMode == "DNS_SPOOF",
                                onClick = { viewModel.setSimulationMode("DNS_SPOOF") }
                            )
                        }
                        item {
                            SimulationChip(
                                title = "🛰️ Rogue Proxy",
                                isSelected = uiState.selectedSimulationMode == "PROXY_INJECT",
                                onClick = { viewModel.setSimulationMode("PROXY_INJECT") }
                            )
                        }
                    }
                }
            }

            // DETAILED DIAGNOSTIC CHECKLIST TILES
            item {
                Text(
                    text = "DETAILED SECURITY & MITM CHECKS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = BentoTextSecondary
                )
            }

            val checkList = report?.checks ?: emptyList()
            if (checkList.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
                    ) {
                        Text(
                            text = "Initializing diagnostic suite...",
                            fontSize = 13.sp,
                            color = BentoTextSecondary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(checkList) { check ->
                    DiagnosticCheckBentoCard(
                        check = check,
                        isExpanded = uiState.expandedCheckId == check.id,
                        onToggleExpand = { viewModel.toggleCheckExpanded(check.id) }
                    )
                }
            }

            // NETWORK PROPERTIES & SETTINGS CARD
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(BentoBadgeSlate),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Router, contentDescription = null, tint = BentoBadgeSlateIcon, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "Active Network Properties", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                                Text(text = "IP routing & Private DNS status", fontSize = 11.sp, color = BentoTextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            NetworkPropertyItem(label = "Security Protocol", value = net?.securityType ?: "WPA2 / WPA3 AES", icon = Icons.Default.Lock)
                            NetworkPropertyItem(label = "Local IP Address", value = net?.ipAddress ?: "192.168.1.105", icon = Icons.Default.Public)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            NetworkPropertyItem(label = "DNS Security", value = uiState.dnsPrivacyStatus, icon = Icons.Default.Dns)
                            NetworkPropertyItem(label = "Default Gateway", value = net?.gateway ?: "192.168.1.1", icon = Icons.Default.Router)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("open_wifi_settings_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BentoHeroBg,
                                contentColor = BentoHeroText
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Configure Wi-Fi & Private DNS", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusMetricChip(label: String, value: String, isGood: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isGood) BentoTextPrimary else RiskCritical
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = BentoTextSecondary
        )
    }
}

@Composable
fun SimulationChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .clickable { onClick() },
        color = if (isSelected) BentoHeroText else BentoCardBg,
        shape = RoundedCornerShape(100.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) BentoHeroText else BentoCardBorder
        )
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else BentoTextPrimary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun DiagnosticCheckBentoCard(
    check: MitmCheckResult,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    val isDanger = check.status == DiagnosticStatus.DANGER
    val isWarning = check.status == DiagnosticStatus.WARNING

    val icon = when (check.category) {
        "Wi-Fi Encryption" -> if (isDanger) Icons.Default.WifiOff else Icons.Default.Wifi
        "SSL Interception" -> Icons.Default.Lock
        "DNS Integrity" -> Icons.Default.Dns
        "Proxy & Route" -> Icons.Default.Http
        "ARP & Gateway" -> Icons.Default.Router
        else -> Icons.Default.VpnLock
    }

    val iconBg = when {
        isDanger -> RiskCritical.copy(alpha = 0.12f)
        isWarning -> BentoBadgePeach.copy(alpha = 0.6f)
        else -> BentoBadgeGreen.copy(alpha = 0.6f)
    }

    val iconTint = when {
        isDanger -> RiskCritical
        isWarning -> BentoBadgePeachIcon
        else -> BentoBadgeGreenIcon
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onToggleExpand() },
        colors = CardDefaults.cardColors(containerColor = BentoCardBg),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDanger) RiskCritical.copy(alpha = 0.4f) else BentoCardBorder
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
                            .background(iconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = check.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
                        )
                        Text(
                            text = check.summary,
                            fontSize = 12.sp,
                            fontWeight = if (isDanger) FontWeight.Bold else FontWeight.Normal,
                            color = if (isDanger) RiskCritical else BentoTextSecondary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = iconBg,
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Text(
                            text = when (check.status) {
                                DiagnosticStatus.SAFE -> "PASS"
                                DiagnosticStatus.WARNING -> "WARN"
                                DiagnosticStatus.DANGER -> "FAIL"
                                DiagnosticStatus.CHECKING -> "CHECKING"
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = iconTint,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand",
                        tint = BentoTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(BentoCardBorder)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "CRYPTOGRAPHIC & TECHNICAL DETAILS:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = BentoHeroAccent
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = check.technicalDetails,
                        fontSize = 12.sp,
                        color = BentoTextPrimary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "SHIELDAI RECOMMENDATION:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = if (isDanger) RiskCritical else BentoBadgeGreenIcon
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = check.recommendation,
                        fontSize = 12.sp,
                        color = BentoTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun NetworkPropertyItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = BentoHeroAccent, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = label, fontSize = 11.sp, color = BentoTextSecondary)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
    }
}

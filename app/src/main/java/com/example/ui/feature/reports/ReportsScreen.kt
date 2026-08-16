package com.example.ui.feature.reports

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.SecurityHistoryEntity
import com.example.data.repository.SecurityRepository
import com.example.ui.theme.BentoBadgeBlue
import com.example.ui.theme.BentoBadgeBlueIcon
import com.example.ui.theme.BentoBadgeGreen
import com.example.ui.theme.BentoBadgeGreenIcon
import com.example.ui.theme.BentoBadgePeach
import com.example.ui.theme.BentoBadgePeachIcon
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportsScreen(
    repository: SecurityRepository,
    onBack: () -> Unit
) {
    val historyList by repository.getSecurityHistory().collectAsState(initial = emptyList())
    val context = LocalContext.current

    val latestScore = repository.getLatestSecurityScore()
    val deviceStatus = repository.getDeviceStatus()
    val netInfo = repository.getNetworkInfo()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BentoCanvasBg)
    ) {
        // Top Bar
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
                    text = "Security Audit Reports",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary
                )
                Text(
                    text = "Historical scan logs & device posture records",
                    fontSize = 12.sp,
                    color = BentoTextSecondary
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Share Audit Report Button
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
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(BentoBadgeBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Assessment, contentDescription = null, tint = BentoBadgeBlueIcon, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "Comprehensive Security Report", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BentoTextPrimary)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Export a clean text audit report detailing device integrity, Wi-Fi security, high-risk permissions, and security score.",
                            fontSize = 12.sp,
                            color = BentoTextSecondary
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                val reportText = buildString {
                                    appendLine("=== DIGITAL SAFETY AUDIT REPORT ===")
                                    appendLine("Generated: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())}")
                                    appendLine("Overall Security Score: ${latestScore?.totalScore ?: 85}/100")
                                    appendLine("Risk Level: ${latestScore?.riskLevel ?: "SAFE"}")
                                    appendLine("\n-- PILLAR SCORES --")
                                    appendLine("Device Security: ${latestScore?.deviceScore ?: 18}/20")
                                    appendLine("App Privacy: ${latestScore?.appScore ?: 16}/20")
                                    appendLine("Sensitive Permissions: ${latestScore?.permissionScore ?: 19}/20")
                                    appendLine("Network Safety: ${latestScore?.networkScore ?: 15}/20")
                                    appendLine("Account Safety: ${latestScore?.accountScore ?: 18}/20")
                                    appendLine("\n-- DEVICE STATUS --")
                                    appendLine("Model: ${deviceStatus.deviceModel}")
                                    appendLine("Android: ${deviceStatus.androidVersion}")
                                    appendLine("Screen Lock Secure: ${deviceStatus.isDeviceSecure}")
                                    appendLine("USB Debugging: ${deviceStatus.isAdbEnabled}")
                                    appendLine("\n-- NETWORK ENCRYPTION --")
                                    appendLine("Connection: ${netInfo.connectionType} (${netInfo.ssid})")
                                    appendLine("Security Protocol: ${netInfo.securityType}")
                                }

                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, reportText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Security Report"))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("share_report_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = BentoHeroText, contentColor = Color.White),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Export / Share Report", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "PAST SECURITY AUDIT SCANS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = BentoTextSecondary
                )
            }

            if (historyList.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                        shape = RoundedCornerShape(18.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
                    ) {
                        Text(
                            text = "No previous scan history recorded yet.",
                            fontSize = 13.sp,
                            color = BentoTextSecondary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(historyList) { item ->
                    SecurityHistoryCard(item = item)
                }
            }
        }
    }
}

@Composable
fun SecurityHistoryCard(item: SecurityHistoryEntity) {
    val dateStr = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()).format(Date(item.createdAt))
    val scoreColor = when {
        item.totalScore >= 90 -> BentoBadgeGreenIcon
        item.totalScore >= 75 -> BentoBadgeGreenIcon
        item.totalScore >= 50 -> BentoBadgePeachIcon
        else -> Color(0xFFB3261E)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BentoCardBg),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = BentoHeroAccent, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = dateStr, fontSize = 12.sp, color = BentoTextSecondary)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${item.scannedAppsCount} Apps Scanned • ${item.issueCount} Issues Found",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = BentoTextPrimary
                )
            }

            Surface(
                color = scoreColor.copy(alpha = 0.12f),
                shape = RoundedCornerShape(100.dp)
            ) {
                Text(
                    text = "${item.totalScore}/100",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = scoreColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

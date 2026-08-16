package com.example.ui.feature.apps

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AppSecurityInfo
import com.example.domain.model.PermissionDetail
import com.example.domain.model.RiskLevel
import com.example.ui.components.RiskBadge
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
import com.example.ui.theme.RiskLow
import com.example.ui.theme.RiskMedium
import com.example.ui.theme.RiskSafe
import com.example.ui.viewmodel.AppsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppsScreen(
    viewModel: AppsViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
            Column {
                Text(
                    text = "App Privacy Auditor",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary
                )
                Text(
                    text = "${uiState.filteredApps.size} apps inspected",
                    fontSize = 12.sp,
                    color = BentoTextSecondary
                )
            }
        }

        // Search Bar
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("app_search_field"),
            placeholder = { Text("Search installed apps or packages...", fontSize = 13.sp, color = BentoTextMuted) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = BentoHeroAccent, modifier = Modifier.size(20.dp))
            },
            trailingIcon = {
                if (uiState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = BentoTextSecondary)
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BentoHeroAccent,
                unfocusedBorderColor = BentoCardBorder,
                focusedContainerColor = BentoCardBg,
                unfocusedContainerColor = BentoCardBg,
                focusedTextColor = BentoTextPrimary,
                unfocusedTextColor = BentoTextPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Chips Row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                AppFilterChip(
                    label = "All (${uiState.allApps.size})",
                    selected = uiState.selectedFilter == "ALL",
                    onClick = { viewModel.setFilter("ALL") }
                )
            }
            item {
                AppFilterChip(
                    label = "High Risk (${uiState.allApps.count { it.riskLevel == RiskLevel.CRITICAL || it.riskLevel == RiskLevel.HIGH }})",
                    selected = uiState.selectedFilter == "HIGH_RISK",
                    onClick = { viewModel.setFilter("HIGH_RISK") },
                    accent = Color(0xFFB3261E)
                )
            }
            item {
                AppFilterChip(
                    label = "User Apps (${uiState.allApps.count { !it.isSystemApp }})",
                    selected = uiState.selectedFilter == "USER",
                    onClick = { viewModel.setFilter("USER") }
                )
            }
            item {
                AppFilterChip(
                    label = "System (${uiState.allApps.count { it.isSystemApp }})",
                    selected = uiState.selectedFilter == "SYSTEM",
                    onClick = { viewModel.setFilter("SYSTEM") }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Apps List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(uiState.filteredApps, key = { it.packageName }) { app ->
                AppListItemCard(
                    app = app,
                    onClick = { viewModel.selectApp(app) }
                )
            }
        }
    }

    // App Detail Bottom Sheet
    uiState.selectedApp?.let { app ->
        ModalBottomSheet(
            onDismissRequest = { viewModel.selectApp(null) },
            sheetState = sheetState,
            containerColor = BentoCardBg,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .size(width = 36.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(BentoCardBorder)
                )
            }
        ) {
            AppDetailSheetContent(
                app = app,
                onOpenSettings = {
                    openAppSystemSettings(context, app.packageName)
                }
            )
        }
    }
}

@Composable
fun AppFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    accent: Color = BentoHeroAccent
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .clickable { onClick() },
        color = if (selected) BentoHeroBg else BentoCardBg,
        shape = RoundedCornerShape(100.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) BentoHeroAccent else BentoCardBorder
        )
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) BentoHeroText else BentoTextSecondary,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun AppListItemCard(
    app: AppSecurityInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("app_item_${app.packageName}"),
        colors = CardDefaults.cardColors(containerColor = BentoCardBg),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (app.riskLevel == RiskLevel.CRITICAL || app.riskLevel == RiskLevel.HIGH) BentoBadgePeach else BentoBadgeBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Android,
                    contentDescription = null,
                    tint = if (app.riskLevel == RiskLevel.CRITICAL || app.riskLevel == RiskLevel.HIGH) BentoBadgePeachIcon else BentoBadgeBlueIcon,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = app.appName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    RiskBadge(riskLevel = app.riskLevel)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = app.packageName,
                    fontSize = 11.sp,
                    color = BentoTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${app.grantedPermissions.size} Granted",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoHeroAccent
                    )
                    Text(
                        text = " • ${app.requestedPermissions.size} Requested",
                        fontSize = 11.sp,
                        color = BentoTextSecondary
                    )
                    if (app.isSystemApp) {
                        Text(
                            text = " • System",
                            fontSize = 11.sp,
                            color = BentoTextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppDetailSheetContent(
    app: AppSecurityInfo,
    onOpenSettings: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(BentoBadgeBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Android,
                        contentDescription = null,
                        tint = BentoBadgeBlueIcon,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.appName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                    Text(
                        text = "v${app.versionName} • Target SDK ${app.targetSdk}",
                        fontSize = 12.sp,
                        color = BentoTextSecondary
                    )
                    Text(
                        text = app.packageName,
                        fontSize = 11.sp,
                        color = BentoHeroAccent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                RiskBadge(riskLevel = app.riskLevel)
            }
        }

        item {
            Button(
                onClick = onOpenSettings,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("open_system_settings_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BentoHeroText,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Manage in Android Settings", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        if (app.riskReasons.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9DEDC)),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF2B8B5))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFB3261E), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Risk Analysis Observations", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF410E0B))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        for (reason in app.riskReasons) {
                            Text("• $reason", fontSize = 12.sp, color = Color(0xFF410E0B), modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "REQUESTED PERMISSIONS (${app.permissionDetails.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = BentoTextSecondary
            )
        }

        items(app.permissionDetails) { perm ->
            PermissionDetailItem(detail = perm)
        }
    }
}

@Composable
fun PermissionDetailItem(detail: PermissionDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BentoCardBg),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = detail.simpleName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary
                )
                Surface(
                    color = if (detail.isGranted) BentoBadgeGreen else Color(0xFFF2F0F4),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Text(
                        text = if (detail.isGranted) "GRANTED" else "NOT GRANTED",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (detail.isGranted) BentoBadgeGreenIcon else BentoTextMuted,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = detail.description, fontSize = 11.sp, color = BentoTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Potential Abuse: ${detail.potentialAbuse}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = if (detail.riskLevel == RiskLevel.CRITICAL || detail.riskLevel == RiskLevel.HIGH) Color(0xFFB3261E) else BentoHeroAccent
            )
        }
    }
}

private fun openAppSystemSettings(context: Context, packageName: String) {
    try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {}
}

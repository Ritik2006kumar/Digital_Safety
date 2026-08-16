package com.example.ui.feature.scam

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VpnLock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.RiskLevel
import com.example.domain.model.ScamAnalysisResult
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
import com.example.ui.viewmodel.ScamViewModel

@Composable
fun ScamScreen(
    viewModel: ScamViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

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
                    text = "Scam & Phishing Shield",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary
                )
                Text(
                    text = "Detect social engineering, fake KYC & malicious URLs",
                    fontSize = 12.sp,
                    color = BentoTextSecondary
                )
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = uiState.selectedTab,
            containerColor = BentoCardBg,
            contentColor = BentoHeroAccent,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab]),
                    color = BentoHeroAccent
                )
            }
        ) {
            Tab(
                selected = uiState.selectedTab == 0,
                onClick = { viewModel.setSelectedTab(0) },
                text = { Text("Message Analyzer", fontSize = 12.sp, fontWeight = if (uiState.selectedTab == 0) FontWeight.Bold else FontWeight.Medium) },
                icon = { Icon(imageVector = Icons.Default.Message, contentDescription = null, modifier = Modifier.size(16.dp)) },
                selectedContentColor = BentoHeroAccent,
                unselectedContentColor = BentoTextSecondary
            )
            Tab(
                selected = uiState.selectedTab == 1,
                onClick = { viewModel.setSelectedTab(1) },
                text = { Text("URL Phishing", fontSize = 12.sp, fontWeight = if (uiState.selectedTab == 1) FontWeight.Bold else FontWeight.Medium) },
                icon = { Icon(imageVector = Icons.Default.Link, contentDescription = null, modifier = Modifier.size(16.dp)) },
                selectedContentColor = BentoHeroAccent,
                unselectedContentColor = BentoTextSecondary
            )
            Tab(
                selected = uiState.selectedTab == 2,
                onClick = { viewModel.setSelectedTab(2) },
                text = { Text("Scam Academy", fontSize = 12.sp, fontWeight = if (uiState.selectedTab == 2) FontWeight.Bold else FontWeight.Medium) },
                icon = { Icon(imageVector = Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp)) },
                selectedContentColor = BentoHeroAccent,
                unselectedContentColor = BentoTextSecondary
            )
        }

        when (uiState.selectedTab) {
            0 -> MessageAnalyzerTab(viewModel = viewModel, uiState = uiState)
            1 -> UrlAnalyzerTab(viewModel = viewModel, uiState = uiState)
            2 -> ScamEducationTab()
        }
    }
}

@Composable
fun MessageAnalyzerTab(
    viewModel: ScamViewModel,
    uiState: com.example.ui.viewmodel.ScamUiState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "PASTE SUSPICIOUS TEXT / SMS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = BentoTextSecondary
            )
        }

        item {
            OutlinedTextField(
                value = uiState.inputMessage,
                onValueChange = { viewModel.setMessageInput(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .testTag("scam_message_input"),
                placeholder = { Text("Paste SMS, WhatsApp alert, or urgent verification email here...", fontSize = 13.sp, color = BentoTextMuted) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BentoHeroAccent,
                    unfocusedBorderColor = BentoCardBorder,
                    focusedContainerColor = BentoCardBg,
                    unfocusedContainerColor = BentoCardBg,
                    focusedTextColor = BentoTextPrimary,
                    unfocusedTextColor = BentoTextPrimary
                ),
                shape = RoundedCornerShape(16.dp)
            )
        }

        item {
            Button(
                onClick = { viewModel.analyzeMessage() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("analyze_message_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = BentoHeroText, contentColor = Color.White),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (uiState.isAnalyzing) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analyze Message", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        item {
            Text(
                text = "TEST WITH REAL-WORLD EXAMPLES:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BentoTextSecondary
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    SampleChip(label = "Fake Bank KYC", onClick = { viewModel.loadSampleScam("BANK_KYC") })
                }
                item {
                    SampleChip(label = "Lottery Prize", onClick = { viewModel.loadSampleScam("LOTTERY") })
                }
                item {
                    SampleChip(label = "Courier Package Hold", onClick = { viewModel.loadSampleScam("COURIER") })
                }
                item {
                    SampleChip(label = "Legit Family Text", onClick = { viewModel.loadSampleScam("SAFE_MSG") })
                }
            }
        }

        uiState.messageResult?.let { result ->
            item {
                ScamResultCard(result = result)
            }
        }
    }
}

@Composable
fun UrlAnalyzerTab(
    viewModel: ScamViewModel,
    uiState: com.example.ui.viewmodel.ScamUiState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "INSPECT WEBSITE LINK / DOMAIN",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = BentoTextSecondary
            )
        }

        item {
            OutlinedTextField(
                value = uiState.inputUrl,
                onValueChange = { viewModel.setUrlInput(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("scam_url_input"),
                placeholder = { Text("https://example-verify-login.xyz/...", fontSize = 13.sp, color = BentoTextMuted) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BentoHeroAccent,
                    unfocusedBorderColor = BentoCardBorder,
                    focusedContainerColor = BentoCardBg,
                    unfocusedContainerColor = BentoCardBg,
                    focusedTextColor = BentoTextPrimary,
                    unfocusedTextColor = BentoTextPrimary
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
        }

        item {
            Button(
                onClick = { viewModel.analyzeUrl() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("analyze_url_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = BentoHeroText, contentColor = Color.White),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (uiState.isAnalyzing) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(imageVector = Icons.Default.Link, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Check URL Reputation", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    SampleChip(label = "PayPal Phishing Spoof", onClick = { viewModel.setUrlInput("http://paypal-security-update.xyz/login") })
                }
                item {
                    SampleChip(label = "Shortened Bit.ly Link", onClick = { viewModel.setUrlInput("http://bit.ly/claim-free-voucher") })
                }
                item {
                    SampleChip(label = "Official Google URL", onClick = { viewModel.setUrlInput("https://accounts.google.com") })
                }
            }
        }

        uiState.urlResult?.let { result ->
            item {
                ScamResultCard(result = result)
            }
        }
    }
}

@Composable
fun ScamResultCard(result: ScamAnalysisResult) {
    val scoreColor = when (result.riskLevel) {
        RiskLevel.CRITICAL -> RiskCritical
        RiskLevel.HIGH -> RiskHigh
        RiskLevel.MEDIUM -> RiskMedium
        else -> BentoBadgeGreenIcon
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BentoCardBg),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = result.scamType.uppercase(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = scoreColor
                    )
                    Text(
                        text = "Scam Probability: ${result.scamScore}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextSecondary
                    )
                }
                RiskBadge(riskLevel = result.riskLevel)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(text = result.explanation, fontSize = 13.sp, color = BentoTextPrimary)

            if (result.detectedIndicators.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "RED FLAGS DETECTED:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB3261E)
                )
                Spacer(modifier = Modifier.height(6.dp))
                for (indicator in result.detectedIndicators) {
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("⚠ ", color = Color(0xFFB3261E), fontSize = 12.sp)
                        Text(indicator, fontSize = 12.sp, color = BentoTextPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "SAFETY INSTRUCTIONS:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BentoBadgeGreenIcon
            )
            Spacer(modifier = Modifier.height(6.dp))
            for (rec in result.safetyRecommendations) {
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("✓ ", color = BentoBadgeGreenIcon, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(rec, fontSize = 12.sp, color = BentoTextPrimary)
                }
            }
        }
    }
}

@Composable
fun SampleChip(label: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .clickable { onClick() },
        color = BentoCardBg,
        shape = RoundedCornerShape(100.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = BentoHeroAccent,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun ScamEducationTab() {
    val lessons = listOf(
        Pair("1. UPI & QR Code Refund Scam", "Scammers claim you received a refund and send a QR code to 'receive' money. Scanning a QR code or entering your UPI PIN ALWAYS DEDUCTS money from your account, never receives."),
        Pair("2. Urgent Bank KYC & Electricity Freeze", "Messages claiming your bank account or electricity will be disconnected within 24 hours are 100% fake social engineering tactics to induce panic."),
        Pair("3. Part-Time Job / Telegram Like Tasks", "Fraudulent schemes offering ₹5000/day for liking YouTube videos or reviewing hotels. They pay small amounts initially, then demand large deposit fees."),
        Pair("4. Sideloaded APK Malware Trojans", "Never download APK files sent via WhatsApp or SMS pretending to be wedding cards, courier tracking apps, or PAN card update tools.")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "COMMON CYBER FRAUD PATTERNS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = BentoTextSecondary
            )
        }

        items(lessons) { (title, desc) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BentoCardBg),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BentoHeroText)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = desc, fontSize = 12.sp, color = BentoTextPrimary)
                }
            }
        }
    }
}


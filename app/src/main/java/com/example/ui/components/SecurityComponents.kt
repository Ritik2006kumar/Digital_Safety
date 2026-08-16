package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.RiskLevel
import com.example.ui.theme.BentoCardBorder
import com.example.ui.theme.BentoHeroAccent
import com.example.ui.theme.BentoHeroText
import com.example.ui.theme.BentoHeroTrack
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.LocalAppColors
import com.example.ui.theme.RiskCritical
import com.example.ui.theme.RiskHigh
import com.example.ui.theme.RiskLow
import com.example.ui.theme.RiskMedium
import com.example.ui.theme.RiskSafe

@Composable
fun SecurityScoreGauge(
    score: Int,
    riskLevel: RiskLevel,
    isScanning: Boolean = false,
    size: Dp = 150.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (isScanning) 1f else score / 100f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "scoreProgress"
    )

    val statusText = when {
        score >= 90 -> "EXCELLENT PROTECTION"
        score >= 75 -> "GOOD SECURITY"
        score >= 50 -> "NEEDS ATTENTION"
        score >= 25 -> "HIGH RISK DETECTED"
        else -> "CRITICAL RISK"
    }

    val heroTrackColor = BentoHeroTrack
    val heroAccentColor = BentoHeroAccent

    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidth = 10.dp.toPx()

            // Background Circle Track
            drawCircle(
                color = heroTrackColor,
                radius = (size.toPx() - strokeWidth) / 2f,
                style = Stroke(width = strokeWidth)
            )

            // Animated Score Arc
            val sweep = 360f * animatedProgress
            drawArc(
                color = heroAccentColor,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = if (isScanning) "--" else "$score",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = BentoHeroText
            )
            Text(
                text = "of 100",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BentoHeroText.copy(alpha = 0.65f)
            )
        }
    }
}

@Composable
fun CategoryScoreBar(
    title: String,
    score: Int,
    maxScore: Int = 20,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val progress = (score.toFloat() / maxScore.toFloat()).coerceIn(0f, 1f)
    val color = BentoHeroAccent

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BentoTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = BentoTextPrimary
                )
            }
            Text(
                text = "$score / $maxScore",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = BentoHeroText
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = BentoHeroAccent,
            trackColor = Color(0xFFE2E8F0),
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
fun RiskBadge(riskLevel: RiskLevel) {
    val colors = LocalAppColors.current
    val (bgColor, textColor, text) = when (riskLevel) {
        RiskLevel.CRITICAL -> if (colors.isDark) {
            Triple(Color(0xFF3E1917), Color(0xFFF87171), "CRITICAL")
        } else {
            Triple(Color(0xFFF9DEDC), Color(0xFFB3261E), "CRITICAL")
        }
        RiskLevel.HIGH -> if (colors.isDark) {
            Triple(Color(0xFF3B1B20), Color(0xFFFB7185), "HIGH RISK")
        } else {
            Triple(Color(0xFFFEE4E2), Color(0xFFBA1A1A), "HIGH RISK")
        }
        RiskLevel.MEDIUM -> if (colors.isDark) {
            Triple(Color(0xFF382318), Color(0xFFFBBF24), "MEDIUM")
        } else {
            Triple(Color(0xFFFEF08A), Color(0xFF854D0E), "MEDIUM")
        }
        RiskLevel.LOW -> if (colors.isDark) {
            Triple(Color(0xFF102D1E), Color(0xFF4ADE80), "LOW RISK")
        } else {
            Triple(Color(0xFFDCFCE7), Color(0xFF166534), "LOW RISK")
        }
        RiskLevel.SAFE -> if (colors.isDark) {
            Triple(Color(0xFF12283E), Color(0xFF38BDF8), "SAFE")
        } else {
            Triple(Color(0xFFD3E3FD), Color(0xFF001D35), "SAFE")
        }
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}


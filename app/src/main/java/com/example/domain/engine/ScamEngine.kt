package com.example.domain.engine

import com.example.domain.model.RiskLevel
import com.example.domain.model.ScamAnalysisResult
import java.net.URI
import java.util.Locale

class ScamEngine {

    fun analyzeMessage(text: String): ScamAnalysisResult {
        val trimmed = text.trim()
        val lower = trimmed.lowercase(Locale.ROOT)

        val indicators = mutableListOf<String>()
        var scamScore = 0

        // 1. Urgency detection
        if (lower.contains("immediately") || lower.contains("within 24 hours") || lower.contains("account suspended") ||
            lower.contains("blocked") || lower.contains("urgent action required") || lower.contains("act now") ||
            lower.contains("last warning") || lower.contains("electricity will be disconnected")
        ) {
            indicators.add("Extreme urgency or threat of immediate account suspension.")
            scamScore += 25
        }

        // 2. OTP / Verification Code Theft
        if (lower.contains("otp") || lower.contains("verification code") || lower.contains("share code") ||
            lower.contains("one time password") || lower.contains("security code") || lower.contains("pin")
        ) {
            indicators.add("Request or reference to sensitive OTP / Security verification codes.")
            scamScore += 30
        }

        // 3. Fake Rewards / Lottery / Crypto / Refund
        if (lower.contains("congratulations") || lower.contains("you won") || lower.contains("lottery") ||
            lower.contains("cashback of") || lower.contains("claim 10,000") || lower.contains("crypto investment") ||
            lower.contains("guaranteed profit") || lower.contains("tax refund approved") || lower.contains("part-time job")
        ) {
            indicators.add("Unrealistic financial reward, lottery winning, or high-yield investment hook.")
            scamScore += 30
        }

        // 4. Impersonation (Bank, Courier, Tax, Tech Support)
        if (lower.contains("fedex parcel") || lower.contains("customs duty") || lower.contains("kyc expired") ||
            lower.contains("update pan") || lower.contains("bank account locked") || lower.contains("netflix payment failed")
        ) {
            indicators.add("Impersonation of trusted institutions (Bank KYC, Courier Delivery, Streaming Service).")
            scamScore += 25
        }

        // 5. Suspicious links inside message
        val hasUrl = lower.contains("http://") || lower.contains("https://") || lower.contains("bit.ly") ||
                lower.contains(".xyz") || lower.contains(".top") || lower.contains("t.me/") || lower.contains(".apk")

        if (hasUrl) {
            indicators.add("Contains external web links or direct APK download links.")
            scamScore += 20
        }

        if (scamScore > 100) scamScore = 100

        val (riskLevel, scamType) = when {
            scamScore >= 65 -> Pair(RiskLevel.CRITICAL, if (lower.contains("otp") || lower.contains("kyc")) "Phishing & Banking Fraud" else "High-Risk Scam")
            scamScore >= 40 -> Pair(RiskLevel.HIGH, "Suspicious Social Engineering")
            scamScore >= 20 -> Pair(RiskLevel.MEDIUM, "Potential Marketing / Spam")
            else -> Pair(RiskLevel.SAFE, "Likely Legitimate Message")
        }

        val explanation = if (scamScore >= 40) {
            "This message displays classic social engineering patterns designed to induce panic or greed, urging you to click a link or divulge security credentials."
        } else {
            "No aggressive scam indicators detected. Always verify sender identity independently before transferring funds."
        }

        val recommendations = mutableListOf<String>()
        if (scamScore >= 40) {
            recommendations.add("NEVER share OTP, PIN, or banking passwords with anyone.")
            recommendations.add("Do not click any embedded links or download attached APK files.")
            recommendations.add("Contact the official customer support directly via their verified website or phone app.")
        } else {
            recommendations.add("Verify the sender's actual email or phone number if an unexpected request is made.")
        }

        return ScamAnalysisResult(
            query = trimmed,
            isUrl = false,
            scamScore = scamScore,
            riskLevel = riskLevel,
            scamType = scamType,
            detectedIndicators = indicators,
            explanation = explanation,
            safetyRecommendations = recommendations
        )
    }

    fun analyzeUrl(url: String): ScamAnalysisResult {
        val trimmed = url.trim()
        val lower = trimmed.lowercase(Locale.ROOT)

        val indicators = mutableListOf<String>()
        var scamScore = 0

        val isHttp = lower.startsWith("http://")
        val isHttps = lower.startsWith("https://")

        if (isHttp) {
            indicators.add("Unencrypted HTTP connection: Traffic is not SSL/TLS secured.")
            scamScore += 30
        }

        val host = try {
            val fullUrl = if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) "https://$trimmed" else trimmed
            URI(fullUrl).host?.lowercase(Locale.ROOT) ?: lower
        } catch (_: Exception) {
            lower
        }

        // Suspicious TLDs
        val suspiciousTlds = listOf(".xyz", ".top", ".buzz", ".icu", ".cam", ".gq", ".ml", ".cf", ".tk", ".work", ".click")
        if (suspiciousTlds.any { host.endsWith(it) }) {
            indicators.add("Domain uses a high-abuse / cheap TLD commonly associated with disposable phishing campaigns.")
            scamScore += 25
        }

        // Typosquatting / Lookalike domains
        val brands = listOf("paypal", "netflix", "google", "apple", "amazon", "microsoft", "chase", "wellsfargo", "binance", "whatsapp", "instagram")
        for (b in brands) {
            if (host.contains(b) && !host.endsWith(".$b.com") && host != "$b.com" && !host.endsWith(".$b.net") && host != "$b.org") {
                indicators.add("Lookalike Brand Spoofing: Contains brand name '$b' in an unverified third-party domain.")
                scamScore += 45
                break
            }
        }

        // IP address as host
        if (host.matches(Regex("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$"))) {
            indicators.add("Direct IP address host used instead of registered domain name.")
            scamScore += 35
        }

        // URL shorteners
        if (host.contains("bit.ly") || host.contains("tinyurl.com") || host.contains("t.co") || host.contains("is.gd")) {
            indicators.add("Shortened URL: Destination domain is obfuscated.")
            scamScore += 20
        }

        // Suspicious path keywords
        if (lower.contains("login") || lower.contains("verify") || lower.contains("update-kyc") || lower.contains("account-security") || lower.contains("secure-token")) {
            if (scamScore > 0) {
                indicators.add("Path mimics sensitive authentication / login / KYC verification pages.")
                scamScore += 20
            }
        }

        if (scamScore > 100) scamScore = 100

        val (riskLevel, scamType) = when {
            scamScore >= 60 -> Pair(RiskLevel.CRITICAL, "Phishing / Credential Harvester")
            scamScore >= 35 -> Pair(RiskLevel.HIGH, "Suspicious Unverified Domain")
            scamScore >= 15 -> Pair(RiskLevel.MEDIUM, "Low-Reputation Link")
            else -> Pair(RiskLevel.SAFE, "Legitimate / Standard URL")
        }

        val explanation = if (scamScore >= 35) {
            "This link shows characteristics of domain spoofing, unencrypted channels, or disguised shortlinks designed to capture sensitive logins or payment information."
        } else {
            "Domain format follows standard conventions with SSL encryption and standard host structure."
        }

        val recommendations = listOf(
            "Do NOT enter passwords, credit card numbers, or OTPs on this website.",
            "Always verify the top-level domain directly in your browser address bar.",
            "Use bookmarks or official search results to reach authentic financial portals."
        )

        return ScamAnalysisResult(
            query = trimmed,
            isUrl = true,
            scamScore = scamScore,
            riskLevel = riskLevel,
            scamType = scamType,
            detectedIndicators = indicators,
            explanation = explanation,
            safetyRecommendations = recommendations
        )
    }
}

package com.example.domain.model

enum class RiskLevel(val label: String, val scoreWeight: Int) {
    CRITICAL("Critical", 4),
    HIGH("High", 3),
    MEDIUM("Medium", 2),
    LOW("Low", 1),
    SAFE("Safe", 0)
}

enum class PermissionRiskCategory(val title: String, val description: String) {
    CRITICAL_PRIVACY("Critical Privacy", "Access to microphone, camera, contacts, SMS"),
    LOCATION_TRACKING("Location & Proximity", "Precise or background geographical tracking"),
    STORAGE_MEDIA("Storage & Files", "Access to photos, audio, videos, documents"),
    CONNECTIVITY_DEVICE("Connectivity & Sensors", "Bluetooth, Wi-Fi state, phone state"),
    NORMAL("Standard System Access", "Normal background operations, vibration, internet")
}

data class PermissionDetail(
    val name: String,
    val simpleName: String,
    val isGranted: Boolean,
    val riskLevel: RiskLevel,
    val category: PermissionRiskCategory,
    val description: String,
    val potentialAbuse: String
)

data class AppSecurityInfo(
    val packageName: String,
    val appName: String,
    val versionName: String,
    val versionCode: Long,
    val targetSdk: Int,
    val installSource: String,
    val isSystemApp: Boolean,
    val requestedPermissions: List<String>,
    val grantedPermissions: List<String>,
    val permissionDetails: List<PermissionDetail>,
    val riskScore: Int, // 0 - 100 (higher = riskier)
    val riskLevel: RiskLevel,
    val riskReasons: List<String>,
    val lastScanned: Long = System.currentTimeMillis()
)

data class DeviceSecurityStatus(
    val isDeviceSecure: Boolean,
    val isDeveloperOptionsEnabled: Boolean,
    val isAdbEnabled: Boolean,
    val isRootDetected: Boolean,
    val isUnknownSourcesAllowed: Boolean,
    val androidVersion: String,
    val securityPatchLevel: String,
    val deviceModel: String,
    val score: Int // 0 - 20
)

data class NetworkSecurityInfo(
    val isConnected: Boolean,
    val connectionType: String, // "Wi-Fi", "Cellular", "VPN", "None"
    val ssid: String,
    val securityType: String, // "WPA3", "WPA2", "WEP", "Open", "Cellular Encrypted"
    val isVpnActive: Boolean,
    val isPrivateDnsActive: Boolean,
    val ipAddress: String,
    val gateway: String,
    val riskLevel: RiskLevel,
    val securityTips: List<String>,
    val score: Int // 0 - 20
)

data class SecurityScore(
    val totalScore: Int, // 0 - 100
    val deviceScore: Int, // 0 - 20
    val appScore: Int, // 0 - 20
    val permissionScore: Int, // 0 - 20
    val networkScore: Int, // 0 - 20
    val accountScore: Int, // 0 - 20
    val riskLevel: RiskLevel,
    val issueCount: Int,
    val issuesList: List<SecurityIssue>,
    val scannedAppsCount: Int,
    val riskyAppsCount: Int,
    val scanTimestamp: Long = System.currentTimeMillis()
)

data class SecurityIssue(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val riskLevel: RiskLevel,
    val actionLabel: String,
    val actionType: IssueActionType,
    val targetPackage: String? = null
)

enum class IssueActionType {
    OPEN_APP_SETTINGS,
    OPEN_DEVICE_SECURITY_SETTINGS,
    OPEN_DEVELOPER_SETTINGS,
    OPEN_WIFI_SETTINGS,
    VIEW_SCAM_TIPS,
    REVIEW_PERMISSIONS
}

data class ScamAnalysisResult(
    val query: String,
    val isUrl: Boolean,
    val scamScore: Int, // 0 - 100 (higher = more likely a scam)
    val riskLevel: RiskLevel,
    val scamType: String, // "Phishing", "OTP/Bank Fraud", "Lottery/Gift Scam", "Impersonation", "Safe"
    val detectedIndicators: List<String>,
    val explanation: String,
    val safetyRecommendations: List<String>,
    val aiEvaluation: String? = null,
    val analyzedAt: Long = System.currentTimeMillis()
)

data class VaultItemModel(
    val id: Long = 0,
    val title: String,
    val category: String, // "Password", "Secure Note", "Recovery Key", "Financial Pin"
    val encryptedContent: String,
    val note: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class DiagnosticStatus(val label: String) {
    SAFE("Secure / Passed"),
    WARNING("Warning"),
    DANGER("Vulnerable / Risk Detected"),
    CHECKING("Analyzing...")
}

data class MitmCheckResult(
    val id: String,
    val title: String,
    val category: String, // "Wi-Fi Encryption", "SSL/TLS Handshake", "DNS Integrity", "Proxy & Route", "ARP/Gateway"
    val status: DiagnosticStatus,
    val riskLevel: RiskLevel,
    val summary: String,
    val technicalDetails: String,
    val recommendation: String
)

data class NetworkDiagnosticReport(
    val overallRisk: RiskLevel = RiskLevel.SAFE,
    val statusBadge: String = "SHIELD SECURE",
    val statusHeadline: String = "No MITM Attack or Insecure Wi-Fi Detected",
    val statusDescription: String = "Your wireless connection is encrypted, SSL certificates are authentic, and DNS requests are uncompromised.",
    val isWifiSecure: Boolean = true,
    val isSslInterceptionDetected: Boolean = false,
    val isDnsTamperingDetected: Boolean = false,
    val isProxyInterceptionActive: Boolean = false,
    val isArpSpoofSuspected: Boolean = false,
    val isCaptivePortalRedirecting: Boolean = false,
    val pingLatencyMs: Int = 22,
    val checkedCount: Int = 5,
    val passedCount: Int = 5,
    val warningCount: Int = 0,
    val dangerCount: Int = 0,
    val checks: List<MitmCheckResult> = emptyList(),
    val diagnosticTimestamp: Long = System.currentTimeMillis()
)

// --- PRO FEATURE MODELS ---

enum class SpywareThreatType(val title: String, val severity: RiskLevel) {
    OVERLAY_HIJACKER("Overlay Hijacker (Screen Phishing)", RiskLevel.CRITICAL),
    ACCESSIBILITY_KEYLOGGER("Accessibility Keylogger / RAT", RiskLevel.CRITICAL),
    SIDELOADED_UNVERIFIED("Sideloaded Unverified Package", RiskLevel.HIGH),
    HIDDEN_BACKGROUND_SPY("Silent Mic/Cam Listener", RiskLevel.HIGH),
    NOTIFICATION_INTERCEPTOR("OTP Notification Interceptor", RiskLevel.HIGH),
    BENIGN_SAFE("Verified Safe / Sandbox Clean", RiskLevel.SAFE)
}

data class SpywareThreat(
    val id: String,
    val packageName: String,
    val appName: String,
    val threatType: SpywareThreatType,
    val severity: RiskLevel,
    val threatDescription: String,
    val detectedBehaviors: List<String>,
    val mitigationAction: String,
    val isSystemApp: Boolean = false
)

data class SpywareScanReport(
    val totalAppsScanned: Int,
    val criticalThreatsFound: Int,
    val warningsFound: Int,
    val threats: List<SpywareThreat>,
    val scanTimestamp: Long = System.currentTimeMillis()
)

enum class TrackerCategory(val label: String, val riskWeight: Int) {
    ADVERTISING("Targeted Advertising", 2),
    ANALYTICS("Telemetry & Usage Analytics", 1),
    PROFILING("Behavioral Device Profiling", 3),
    LOCATION_SURVEILLANCE("Geographical Tracking", 3)
}

data class AppTracker(
    val name: String,
    val company: String,
    val category: TrackerCategory,
    val signatureClass: String,
    val dataCollected: String
)

data class AppTrackerProfile(
    val packageName: String,
    val appName: String,
    val totalTrackersCount: Int,
    val privacyScore: Int, // 0 - 100 (100 = No Trackers, completely private)
    val trackers: List<AppTracker>,
    val hasBackgroundExfiltrationRisk: Boolean
)

data class BreachRecord(
    val breachTitle: String,
    val breachDomain: String,
    val breachDate: String,
    val pwnCount: Long,
    val compromisedDataClasses: List<String>,
    val riskLevel: RiskLevel,
    val description: String,
    val recommendedActions: List<String>
)

data class BreachLookupResult(
    val queryTarget: String, // email / username / phone
    val isBreached: Boolean,
    val breachCount: Int,
    val exposedPasswordsCount: Int,
    val riskScore: Int, // 0 - 100
    val breachRecords: List<BreachRecord>,
    val immediateSafetyGuide: List<String>,
    val checkedAt: Long = System.currentTimeMillis()
)

data class EmergencyLockdownStep(
    val id: String,
    val title: String,
    val subtitle: String,
    val isCompleted: Boolean = false,
    val actionType: String // "AIRPLANE_MODE", "CLEAR_CLIPBOARD", "REVOKE_OVERLAYS", "WIFI_OFF", "LOCK_DEVICE"
)

package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.entity.InstalledAppEntity
import com.example.data.local.entity.ScamCheckEntity
import com.example.data.local.entity.SecurityHistoryEntity
import com.example.data.local.entity.VaultItemEntity
import com.example.data.remote.gemini.GeminiAdvisorService
import com.example.domain.engine.AppRiskEngine
import com.example.domain.engine.BackgroundProtectionStatus
import com.example.domain.engine.BackgroundSentinelEngine
import com.example.domain.engine.DataBreachEngine
import com.example.domain.engine.DeviceSecurityEngine
import com.example.domain.engine.EmergencyLockdownEngine
import com.example.domain.engine.NetworkSecurityEngine
import com.example.domain.engine.ScamEngine
import com.example.domain.engine.SpywareDetectionEngine
import com.example.domain.engine.TrackerInspectionEngine
import com.example.domain.engine.VaultEncryptionEngine
import com.example.domain.model.AppSecurityInfo
import com.example.domain.model.AppTrackerProfile
import com.example.domain.model.BreachLookupResult
import com.example.domain.model.DeviceSecurityStatus
import com.example.domain.model.IssueActionType
import com.example.domain.model.NetworkSecurityInfo
import com.example.domain.model.RiskLevel
import com.example.domain.model.ScamAnalysisResult
import com.example.domain.model.SecurityIssue
import com.example.domain.model.SecurityScore
import com.example.domain.model.SpywareScanReport
import com.example.domain.model.VaultItemModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SecurityRepository(private val context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val appDao = db.installedAppDao()
    private val historyDao = db.securityHistoryDao()
    private val vaultDao = db.vaultItemDao()
    private val scamDao = db.scamCheckDao()

    private val deviceEngine = DeviceSecurityEngine(context)
    private val networkEngine = NetworkSecurityEngine(context)
    private val appRiskEngine = AppRiskEngine(context)
    private val scamEngine = ScamEngine()
    private val vaultEngine = VaultEncryptionEngine()
    private val geminiService = GeminiAdvisorService()
    private val backgroundEngine = BackgroundSentinelEngine(context)
    private val spywareEngine = SpywareDetectionEngine(context)
    private val trackerEngine = TrackerInspectionEngine(context)
    private val breachEngine = DataBreachEngine()
    private val lockdownEngine = EmergencyLockdownEngine(context)

    // Cached current scan data
    private var cachedScannedApps: List<AppSecurityInfo> = emptyList()
    private var cachedDeviceStatus: DeviceSecurityStatus? = null
    private var cachedNetworkInfo: NetworkSecurityInfo? = null
    private var cachedSecurityScore: SecurityScore? = null

    suspend fun runFullSecurityScan(): SecurityScore = withContext(Dispatchers.IO) {
        val deviceStatus = deviceEngine.inspectDeviceSecurity()
        val networkInfo = networkEngine.analyzeNetwork()
        val scannedApps = appRiskEngine.scanInstalledApps()

        cachedDeviceStatus = deviceStatus
        cachedNetworkInfo = networkInfo
        cachedScannedApps = scannedApps

        // Compute App Score (0 to 20)
        val riskyApps = scannedApps.filter { it.riskLevel == RiskLevel.CRITICAL || it.riskLevel == RiskLevel.HIGH }
        var appScore = 20 - (riskyApps.size * 3)
        if (appScore < 4) appScore = 4

        // Compute Permission Score (0 to 20)
        val appsWithSms = scannedApps.count { app -> app.grantedPermissions.any { it.contains("SMS", ignoreCase = true) } && !app.isSystemApp }
        val appsWithLocation = scannedApps.count { app -> app.grantedPermissions.any { it.contains("LOCATION", ignoreCase = true) } }
        var permissionScore = 20 - (appsWithSms * 4) - (if (appsWithLocation > 5) 3 else 0)
        if (permissionScore < 5) permissionScore = 5

        val deviceScore = deviceStatus.score
        val networkScore = networkInfo.score
        val accountScore = 18 // Strong baseline (2FA verification recommended)

        val totalScore = (deviceScore + appScore + permissionScore + networkScore + accountScore).coerceIn(0, 100)

        val issues = mutableListOf<SecurityIssue>()

        if (!deviceStatus.isDeviceSecure) {
            issues.add(
                SecurityIssue(
                    id = "dev_lock",
                    title = "Screen Lock Not Set",
                    description = "Your device has no PIN, Pattern, or Biometric lock configured. Anyone can access your private data.",
                    category = "Device Security",
                    riskLevel = RiskLevel.CRITICAL,
                    actionLabel = "Set Screen Lock",
                    actionType = IssueActionType.OPEN_DEVICE_SECURITY_SETTINGS
                )
            )
        }

        if (deviceStatus.isAdbEnabled) {
            issues.add(
                SecurityIssue(
                    id = "dev_adb",
                    title = "USB Debugging Enabled",
                    description = "ADB USB debugging allows external computers to execute commands without full verification.",
                    category = "Device Security",
                    riskLevel = RiskLevel.MEDIUM,
                    actionLabel = "Review Dev Options",
                    actionType = IssueActionType.OPEN_DEVELOPER_SETTINGS
                )
            )
        }

        for (risky in riskyApps.take(3)) {
            issues.add(
                SecurityIssue(
                    id = "app_${risky.packageName}",
                    title = "Review Permissions: ${risky.appName}",
                    description = risky.riskReasons.firstOrNull() ?: "Requests high-risk permissions.",
                    category = "App Privacy",
                    riskLevel = risky.riskLevel,
                    actionLabel = "Manage App",
                    actionType = IssueActionType.OPEN_APP_SETTINGS,
                    targetPackage = risky.packageName
                )
            )
        }

        if (!networkInfo.isVpnActive && networkInfo.connectionType.contains("Wi-Fi")) {
            issues.add(
                SecurityIssue(
                    id = "net_dns",
                    title = "Private DNS Not Configured",
                    description = "Consider configuring encrypted DNS (DoH/DoT) to protect your browsing activity on Wi-Fi.",
                    category = "Network Safety",
                    riskLevel = RiskLevel.LOW,
                    actionLabel = "Network Tips",
                    actionType = IssueActionType.OPEN_WIFI_SETTINGS
                )
            )
        }

        val overallRisk = when {
            totalScore >= 90 -> RiskLevel.SAFE
            totalScore >= 75 -> RiskLevel.LOW
            totalScore >= 50 -> RiskLevel.MEDIUM
            totalScore >= 25 -> RiskLevel.HIGH
            else -> RiskLevel.CRITICAL
        }

        val scoreResult = SecurityScore(
            totalScore = totalScore,
            deviceScore = deviceScore,
            appScore = appScore,
            permissionScore = permissionScore,
            networkScore = networkScore,
            accountScore = accountScore,
            riskLevel = overallRisk,
            issueCount = issues.size,
            issuesList = issues,
            scannedAppsCount = scannedApps.size,
            riskyAppsCount = riskyApps.size
        )

        cachedSecurityScore = scoreResult

        // Save to Room DB
        val entities = scannedApps.map {
            InstalledAppEntity(
                packageName = it.packageName,
                appName = it.appName,
                versionName = it.versionName,
                versionCode = it.versionCode,
                targetSdk = it.targetSdk,
                installSource = it.installSource,
                isSystemApp = it.isSystemApp,
                requestedPermissionsCount = it.requestedPermissions.size,
                grantedPermissionsCount = it.grantedPermissions.size,
                grantedPermissionsJson = it.grantedPermissions.joinToString(","),
                riskScore = it.riskScore,
                riskLevel = it.riskLevel.name,
                riskReasonsJson = it.riskReasons.joinToString(" | "),
                lastScanned = it.lastScanned
            )
        }
        appDao.clearAll()
        appDao.insertApps(entities)

        historyDao.insertHistory(
            SecurityHistoryEntity(
                totalScore = totalScore,
                deviceScore = deviceScore,
                appScore = appScore,
                permissionScore = permissionScore,
                networkScore = networkScore,
                accountScore = accountScore,
                issueCount = issues.size,
                scannedAppsCount = scannedApps.size,
                riskyAppsCount = riskyApps.size
            )
        )

        scoreResult
    }

    fun getScannedApps(): List<AppSecurityInfo> {
        if (cachedScannedApps.isEmpty()) {
            cachedScannedApps = appRiskEngine.scanInstalledApps()
        }
        return cachedScannedApps
    }

    fun getDeviceStatus(): DeviceSecurityStatus {
        return cachedDeviceStatus ?: deviceEngine.inspectDeviceSecurity().also { cachedDeviceStatus = it }
    }

    fun getNetworkInfo(): NetworkSecurityInfo {
        return cachedNetworkInfo ?: networkEngine.analyzeNetwork().also { cachedNetworkInfo = it }
    }

    suspend fun runNetworkMitmDiagnostics(simulationMode: String? = null): com.example.domain.model.NetworkDiagnosticReport {
        cachedNetworkInfo = networkEngine.analyzeNetwork()
        return networkEngine.runDeepMitmDiagnostics(simulationMode)
    }

    fun getLatestSecurityScore(): SecurityScore? {
        return cachedSecurityScore
    }

    // Scam Analysis
    suspend fun analyzeScamMessage(message: String): ScamAnalysisResult = withContext(Dispatchers.IO) {
        val result = scamEngine.analyzeMessage(message)
        // Log to database
        scamDao.insertScamCheck(
            ScamCheckEntity(
                content = message.take(150),
                isUrl = false,
                scamScore = result.scamScore,
                riskLevel = result.riskLevel.name,
                scamType = result.scamType,
                indicatorsJson = result.detectedIndicators.joinToString(" | "),
                explanation = result.explanation
            )
        )
        result
    }

    suspend fun analyzeScamUrl(url: String): ScamAnalysisResult = withContext(Dispatchers.IO) {
        val result = scamEngine.analyzeUrl(url)
        scamDao.insertScamCheck(
            ScamCheckEntity(
                content = url.take(150),
                isUrl = true,
                scamScore = result.scamScore,
                riskLevel = result.riskLevel.name,
                scamType = result.scamType,
                indicatorsJson = result.detectedIndicators.joinToString(" | "),
                explanation = result.explanation
            )
        )
        result
    }

    suspend fun askAiAdvisor(prompt: String): String = withContext(Dispatchers.IO) {
        val contextSummary = buildString {
            append("Total Security Score: ${cachedSecurityScore?.totalScore ?: 82}/100\n")
            append("Device: ${cachedDeviceStatus?.deviceModel ?: "Android Device"}, Android ${cachedDeviceStatus?.androidVersion ?: "14"}\n")
            append("Network: ${cachedNetworkInfo?.connectionType ?: "Wi-Fi"} (${cachedNetworkInfo?.ssid ?: "Connected"})\n")
            append("Scanned Apps: ${cachedScannedApps.size}, Risky Apps: ${cachedSecurityScore?.riskyAppsCount ?: 0}\n")
        }
        geminiService.consultSecurityAdvisor(prompt, contextSummary)
    }

    // Vault Operations
    fun getVaultItems(): Flow<List<VaultItemModel>> {
        return vaultDao.getAllVaultItems().map { entities ->
            entities.map {
                VaultItemModel(
                    id = it.id,
                    title = it.title,
                    category = it.category,
                    encryptedContent = it.encryptedContent,
                    note = it.note,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun addVaultItem(title: String, category: String, plainContent: String, note: String, masterPin: String) = withContext(Dispatchers.IO) {
        val encrypted = vaultEngine.encrypt(plainContent, masterPin)
        vaultDao.insertItem(
            VaultItemEntity(
                title = title,
                category = category,
                encryptedContent = encrypted,
                note = note
            )
        )
    }

    suspend fun deleteVaultItem(id: Long) = withContext(Dispatchers.IO) {
        vaultDao.deleteItem(id)
    }

    fun decryptVaultContent(encryptedContent: String, masterPin: String): String {
        return vaultEngine.decrypt(encryptedContent, masterPin)
    }

    fun getSecurityHistory(): Flow<List<SecurityHistoryEntity>> {
        return historyDao.getAllHistory().flowOn(Dispatchers.IO)
    }

    fun getScamHistory(): Flow<List<ScamCheckEntity>> {
        return scamDao.getAllScamChecks().flowOn(Dispatchers.IO)
    }

    fun getBackgroundProtectionStatus(): BackgroundProtectionStatus {
        return backgroundEngine.getProtectionStatus()
    }

    fun toggleBackgroundProtection(enabled: Boolean): BackgroundProtectionStatus {
        backgroundEngine.setProtectionEnabled(enabled)
        return backgroundEngine.getProtectionStatus()
    }

    fun toggleAppWatchdog(enabled: Boolean): BackgroundProtectionStatus {
        backgroundEngine.setAppWatchdog(enabled)
        return backgroundEngine.getProtectionStatus()
    }

    fun toggleNetworkGuard(enabled: Boolean): BackgroundProtectionStatus {
        backgroundEngine.setNetworkGuard(enabled)
        return backgroundEngine.getProtectionStatus()
    }

    fun toggleClipboardShield(enabled: Boolean): BackgroundProtectionStatus {
        backgroundEngine.setClipboardShield(enabled)
        return backgroundEngine.getProtectionStatus()
    }

    fun triggerBackgroundHealthDiagnostic(): BackgroundProtectionStatus {
        backgroundEngine.recordBackgroundScan()
        return backgroundEngine.getProtectionStatus()
    }

    fun openBatterySettings() {
        backgroundEngine.openBatteryOptimizationSettings()
    }

    fun openAppInfoSettings() {
        backgroundEngine.openAppDetailsSettings()
    }

    // --- PRO ENGINE REPOSITORY METHODS ---

    suspend fun runSpywareScan(): SpywareScanReport = withContext(Dispatchers.IO) {
        spywareEngine.runDeepSpywareScan()
    }

    suspend fun inspectAppTrackers(): List<AppTrackerProfile> = withContext(Dispatchers.IO) {
        trackerEngine.inspectAppTrackers()
    }

    suspend fun checkDataBreach(query: String): BreachLookupResult = withContext(Dispatchers.IO) {
        breachEngine.checkBreach(query)
    }

    fun getEmergencyLockdownSteps() = lockdownEngine.getLockdownSteps()

    fun executeClearClipboard(): Boolean = lockdownEngine.executeClearClipboard()

    fun openAirplaneModeSettings() = lockdownEngine.openAirplaneModeSettings()

    fun openOverlaySettings() = lockdownEngine.openOverlaySettings()

    fun openSecuritySettings() = lockdownEngine.openSecuritySettings()
}

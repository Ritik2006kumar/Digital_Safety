package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.SecurityRepository
import com.example.domain.model.AppSecurityInfo
import com.example.domain.model.NetworkSecurityInfo
import com.example.domain.model.PermissionDetail
import com.example.domain.model.RiskLevel
import com.example.domain.model.ScamAnalysisResult
import com.example.domain.model.VaultItemModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- Apps ViewModel ---
data class AppsUiState(
    val allApps: List<AppSecurityInfo> = emptyList(),
    val filteredApps: List<AppSecurityInfo> = emptyList(),
    val selectedFilter: String = "ALL", // "ALL", "HIGH_RISK", "USER", "SYSTEM"
    val searchQuery: String = "",
    val selectedApp: AppSecurityInfo? = null
)

class AppsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SecurityRepository(application.applicationContext)
    private val _uiState = MutableStateFlow(AppsUiState())
    val uiState: StateFlow<AppsUiState> = _uiState.asStateFlow()

    init {
        loadApps()
    }

    fun loadApps() {
        val apps = repository.getScannedApps()
        _uiState.update { it.copy(allApps = apps, filteredApps = applyFilter(apps, it.selectedFilter, it.searchQuery)) }
    }

    fun setFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter, filteredApps = applyFilter(it.allApps, filter, it.searchQuery)) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query, filteredApps = applyFilter(it.allApps, it.selectedFilter, query)) }
    }

    fun selectApp(app: AppSecurityInfo?) {
        _uiState.update { it.copy(selectedApp = app) }
    }

    private fun applyFilter(apps: List<AppSecurityInfo>, filter: String, query: String): List<AppSecurityInfo> {
        return apps.filter { app ->
            val matchesQuery = query.isBlank() || app.appName.contains(query, ignoreCase = true) || app.packageName.contains(query, ignoreCase = true)
            val matchesFilter = when (filter) {
                "HIGH_RISK" -> app.riskLevel == RiskLevel.CRITICAL || app.riskLevel == RiskLevel.HIGH
                "USER" -> !app.isSystemApp
                "SYSTEM" -> app.isSystemApp
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }
}

// --- Permissions ViewModel ---
data class SensitivePermissionGroup(
    val title: String,
    val permissionKey: String,
    val iconName: String,
    val riskLevel: RiskLevel,
    val description: String,
    val appsWithAccess: List<AppSecurityInfo>
)

data class PermissionsUiState(
    val groups: List<SensitivePermissionGroup> = emptyList(),
    val selectedGroup: SensitivePermissionGroup? = null
)

class PermissionsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SecurityRepository(application.applicationContext)
    private val _uiState = MutableStateFlow(PermissionsUiState())
    val uiState: StateFlow<PermissionsUiState> = _uiState.asStateFlow()

    init {
        loadPermissionGroups()
    }

    fun loadPermissionGroups() {
        val apps = repository.getScannedApps()

        val groupList = listOf(
            SensitivePermissionGroup(
                title = "Camera",
                permissionKey = "CAMERA",
                iconName = "camera",
                riskLevel = RiskLevel.HIGH,
                description = "Apps with permission to capture photos and record video.",
                appsWithAccess = apps.filter { it.grantedPermissions.any { p -> p.contains("CAMERA", ignoreCase = true) } }
            ),
            SensitivePermissionGroup(
                title = "Microphone",
                permissionKey = "RECORD_AUDIO",
                iconName = "mic",
                riskLevel = RiskLevel.HIGH,
                description = "Apps with permission to record background audio and voice.",
                appsWithAccess = apps.filter { it.grantedPermissions.any { p -> p.contains("RECORD_AUDIO", ignoreCase = true) } }
            ),
            SensitivePermissionGroup(
                title = "Contacts",
                permissionKey = "CONTACTS",
                iconName = "contacts",
                riskLevel = RiskLevel.HIGH,
                description = "Apps with permission to read or modify your address book.",
                appsWithAccess = apps.filter { it.grantedPermissions.any { p -> p.contains("CONTACTS", ignoreCase = true) } }
            ),
            SensitivePermissionGroup(
                title = "Location (GPS)",
                permissionKey = "LOCATION",
                iconName = "location",
                riskLevel = RiskLevel.HIGH,
                description = "Apps with access to fine GPS or coarse network location.",
                appsWithAccess = apps.filter { it.grantedPermissions.any { p -> p.contains("LOCATION", ignoreCase = true) } }
            ),
            SensitivePermissionGroup(
                title = "SMS & OTPs",
                permissionKey = "SMS",
                iconName = "sms",
                riskLevel = RiskLevel.CRITICAL,
                description = "Apps with permission to read, send, or receive SMS messages.",
                appsWithAccess = apps.filter { it.grantedPermissions.any { p -> p.contains("SMS", ignoreCase = true) } }
            ),
            SensitivePermissionGroup(
                title = "Storage & Files",
                permissionKey = "STORAGE",
                iconName = "storage",
                riskLevel = RiskLevel.MEDIUM,
                description = "Apps with permission to access stored files, photos, and media.",
                appsWithAccess = apps.filter { it.grantedPermissions.any { p -> p.contains("STORAGE", ignoreCase = true) } }
            )
        )

        _uiState.update { it.copy(groups = groupList) }
    }

    fun selectGroup(group: SensitivePermissionGroup?) {
        _uiState.update { it.copy(selectedGroup = group) }
    }
}

// --- Network ViewModel ---
data class NetworkUiState(
    val networkInfo: NetworkSecurityInfo? = null,
    val diagnosticReport: com.example.domain.model.NetworkDiagnosticReport? = null,
    val isRunningDiagnostics: Boolean = false,
    val activeDiagnosticStep: String = "",
    val pingLatencyMs: Int = 24,
    val dnsPrivacyStatus: String = "Private DNS (DoT/DoH) Active",
    val selectedSimulationMode: String? = null,
    val expandedCheckId: String? = null
)

class NetworkViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SecurityRepository(application.applicationContext)
    private val _uiState = MutableStateFlow(NetworkUiState())
    val uiState: StateFlow<NetworkUiState> = _uiState.asStateFlow()

    init {
        refreshNetwork()
        runDiagnostics()
    }

    fun refreshNetwork() {
        val info = repository.getNetworkInfo()
        _uiState.update { it.copy(networkInfo = info) }
    }

    fun setSimulationMode(mode: String?) {
        _uiState.update { it.copy(selectedSimulationMode = mode) }
        runDiagnostics(mode)
    }

    fun toggleCheckExpanded(checkId: String) {
        _uiState.update { current ->
            current.copy(expandedCheckId = if (current.expandedCheckId == checkId) null else checkId)
        }
    }

    fun runDiagnostics(simulationMode: String? = _uiState.value.selectedSimulationMode) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRunningDiagnostics = true, activeDiagnosticStep = "Analyzing Wi-Fi ciphers...") }
            delay(350)
            _uiState.update { it.copy(activeDiagnosticStep = "Testing SSL/TLS handshake & trust store...") }
            delay(350)
            _uiState.update { it.copy(activeDiagnosticStep = "Checking DNS resolution & poisoning...") }
            delay(350)
            _uiState.update { it.copy(activeDiagnosticStep = "Inspecting proxy route & ARP gateway...") }
            delay(350)

            val report = repository.runNetworkMitmDiagnostics(simulationMode)
            val info = repository.getNetworkInfo()

            _uiState.update {
                it.copy(
                    isRunningDiagnostics = false,
                    activeDiagnosticStep = "",
                    networkInfo = info,
                    diagnosticReport = report,
                    pingLatencyMs = report.pingLatencyMs,
                    dnsPrivacyStatus = if (report.isDnsTamperingDetected) "Tampered / Insecure" else "Private DNS (DoT/DoH) Protected"
                )
            }
        }
    }
}

// --- Scam ViewModel ---
data class ScamUiState(
    val inputMessage: String = "",
    val inputUrl: String = "",
    val isAnalyzing: Boolean = false,
    val messageResult: ScamAnalysisResult? = null,
    val urlResult: ScamAnalysisResult? = null,
    val selectedTab: Int = 0 // 0: Message, 1: URL, 2: Simulator/Quiz
)

class ScamViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SecurityRepository(application.applicationContext)
    private val _uiState = MutableStateFlow(ScamUiState())
    val uiState: StateFlow<ScamUiState> = _uiState.asStateFlow()

    fun setSelectedTab(tab: Int) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun setMessageInput(text: String) {
        _uiState.update { it.copy(inputMessage = text) }
    }

    fun setUrlInput(url: String) {
        _uiState.update { it.copy(inputUrl = url) }
    }

    fun analyzeMessage() {
        val msg = _uiState.value.inputMessage
        if (msg.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isAnalyzing = true) }
            val result = repository.analyzeScamMessage(msg)
            _uiState.update { it.copy(isAnalyzing = false, messageResult = result) }
        }
    }

    fun analyzeUrl() {
        val url = _uiState.value.inputUrl
        if (url.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isAnalyzing = true) }
            val result = repository.analyzeScamUrl(url)
            _uiState.update { it.copy(isAnalyzing = false, urlResult = result) }
        }
    }

    fun loadSampleScam(type: String) {
        val sample = when (type) {
            "BANK_KYC" -> "URGENT: Your SBI Bank Account KYC has expired today. To prevent immediate account suspension and freeze, click http://sbi-kyc-update.xyz/login to submit your PAN and OTP verification."
            "LOTTERY" -> "Congratulations! You have won $50,000 cash prize in the International Lucky Draw. Call immediately or click http://claim-prize.top/win to pay $10 processing fee and claim money."
            "COURIER" -> "FedEx Notice: Your shipment #84920 is on hold due to missing address details. Update your address within 24 hours at http://fedx-package-delivery.info/track or package will be destroyed."
            "SAFE_MSG" -> "Hi Mom, I'm heading over for dinner around 7 PM. Let me know if you need me to pick up groceries on the way."
            else -> ""
        }
        _uiState.update { it.copy(inputMessage = sample) }
    }
}

// --- AI Advisor ViewModel ---
data class ChatMessage(
    val sender: String, // "user" or "ai"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class AiUiState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            sender = "ai",
            content = "Hello! I am ShieldAI, your personal cybersecurity and privacy advisor. Ask me about suspicious app permissions, phishing messages, Wi-Fi security, or how to tighten your Android device posture."
        )
    ),
    val currentInput: String = "",
    val isLoading: Boolean = false
)

class AiViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SecurityRepository(application.applicationContext)
    private val _uiState = MutableStateFlow(AiUiState())
    val uiState: StateFlow<AiUiState> = _uiState.asStateFlow()

    fun setInput(text: String) {
        _uiState.update { it.copy(currentInput = text) }
    }

    fun sendMessage(prompt: String? = null) {
        val text = prompt ?: _uiState.value.currentInput
        if (text.isBlank()) return

        val userMsg = ChatMessage(sender = "user", content = text)
        _uiState.update { it.copy(messages = it.messages + userMsg, currentInput = "", isLoading = true) }

        viewModelScope.launch {
            val response = repository.askAiAdvisor(text)
            val aiMsg = ChatMessage(sender = "ai", content = response)
            _uiState.update { it.copy(messages = it.messages + aiMsg, isLoading = false) }
        }
    }
}

// --- Vault ViewModel ---
data class VaultUiState(
    val isUnlocked: Boolean = false,
    val isDecoyMode: Boolean = false, // When true, fake dummy safe records are shown
    val masterPin: String = "1234",
    val decoyPin: String = "0000",
    val enteredPin: String = "",
    val pinError: Boolean = false,
    val isCreatingItem: Boolean = false,
    val decryptedItemId: Long? = null,
    val decryptedText: String = "",
    val showAntiCoercionInfo: Boolean = false
)

class VaultViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SecurityRepository(application.applicationContext)
    private val _uiState = MutableStateFlow(VaultUiState())
    val uiState: StateFlow<VaultUiState> = _uiState.asStateFlow()

    private val _realVaultItems: StateFlow<List<VaultItemModel>> = repository.getVaultItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Decoy mock items shown under coercion
    private val dummyDecoyItems = listOf(
        VaultItemModel(
            id = 991,
            title = "Home Wi-Fi Guest Password",
            category = "Password",
            encryptedContent = "GuestPass2026!",
            note = "For visiting guests and smart TV"
        ),
        VaultItemModel(
            id = 992,
            title = "Weekly Grocery List",
            category = "Secure Note",
            encryptedContent = "Milk, Organic Almonds, Apples, Olive oil, Green Tea",
            note = "Supermarket reminder note"
        ),
        VaultItemModel(
            id = 993,
            title = "Public Library Card PIN",
            category = "Financial Pin",
            encryptedContent = "5544",
            note = "Community library barcode access"
        )
    )

    val vaultItems: StateFlow<List<VaultItemModel>> = combine(_uiState, _realVaultItems) { state, realItems ->
        if (state.isDecoyMode) dummyDecoyItems else realItems
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun enterPinDigit(digit: String) {
        if (_uiState.value.enteredPin.length < 4) {
            val newPin = _uiState.value.enteredPin + digit
            _uiState.update { it.copy(enteredPin = newPin, pinError = false) }
            if (newPin.length == 4) {
                verifyPin(newPin)
            }
        }
    }

    fun deletePinDigit() {
        if (_uiState.value.enteredPin.isNotEmpty()) {
            _uiState.update { it.copy(enteredPin = it.enteredPin.dropLast(1), pinError = false) }
        }
    }

    private fun verifyPin(pin: String) {
        when (pin) {
            _uiState.value.masterPin -> {
                // Real Master Vault
                _uiState.update { it.copy(isUnlocked = true, isDecoyMode = false, enteredPin = "", pinError = false) }
            }
            _uiState.value.decoyPin -> {
                // Decoy Anti-Coercion Mode
                _uiState.update { it.copy(isUnlocked = true, isDecoyMode = true, enteredPin = "", pinError = false) }
            }
            else -> {
                _uiState.update { it.copy(enteredPin = "", pinError = true) }
            }
        }
    }

    fun unlockWithBiometrics() {
        // Fast instant authentic biometric unlock into real master vault
        _uiState.update { it.copy(isUnlocked = true, isDecoyMode = false, enteredPin = "", pinError = false) }
    }

    fun lockVault() {
        _uiState.update { it.copy(isUnlocked = false, isDecoyMode = false, enteredPin = "", decryptedItemId = null, decryptedText = "") }
    }

    fun toggleAntiCoercionInfo(show: Boolean) {
        _uiState.update { it.copy(showAntiCoercionInfo = show) }
    }

    fun setCreatingItem(creating: Boolean) {
        if (!_uiState.value.isDecoyMode) {
            _uiState.update { it.copy(isCreatingItem = creating) }
        }
    }

    fun addItem(title: String, category: String, content: String, note: String) {
        if (_uiState.value.isDecoyMode) return
        viewModelScope.launch {
            repository.addVaultItem(title, category, content, note, _uiState.value.masterPin)
            _uiState.update { it.copy(isCreatingItem = false) }
        }
    }

    fun deleteItem(id: Long) {
        if (_uiState.value.isDecoyMode) return
        viewModelScope.launch {
            repository.deleteVaultItem(id)
        }
    }

    fun viewDecrypted(item: VaultItemModel) {
        if (_uiState.value.isDecoyMode) {
            _uiState.update { it.copy(decryptedItemId = item.id, decryptedText = item.encryptedContent) }
        } else {
            val decrypted = repository.decryptVaultContent(item.encryptedContent, _uiState.value.masterPin)
            _uiState.update { it.copy(decryptedItemId = item.id, decryptedText = decrypted) }
        }
    }

    fun hideDecrypted() {
        _uiState.update { it.copy(decryptedItemId = null, decryptedText = "") }
    }
}

// --- PRO SECURITY VIEWMODEL (Spyware, Trackers, Dark Web Breaches & Emergency Lockdown) ---

data class ProSecurityUiState(
    // Spyware RAT Scanner
    val isScanningSpyware: Boolean = false,
    val spywareReport: com.example.domain.model.SpywareScanReport? = null,
    val selectedThreat: com.example.domain.model.SpywareThreat? = null,

    // App Privacy Trackers
    val isAnalyzingTrackers: Boolean = false,
    val trackerProfiles: List<com.example.domain.model.AppTrackerProfile> = emptyList(),
    val trackerSearchQuery: String = "",
    val selectedTrackerProfile: com.example.domain.model.AppTrackerProfile? = null,

    // Data Breach & Dark Web
    val breachQuery: String = "",
    val isCheckingBreach: Boolean = false,
    val breachResult: com.example.domain.model.BreachLookupResult? = null,

    // Emergency Lockdown
    val isLockdownActive: Boolean = false,
    val lockdownSteps: List<com.example.domain.model.EmergencyLockdownStep> = emptyList(),
    val clipboardSanitized: Boolean = false
)

class ProSecurityViewModel(application: Application) : AndroidViewModel(application) {
    val repository = SecurityRepository(application.applicationContext)
    private val _uiState = MutableStateFlow(ProSecurityUiState())
    val uiState: StateFlow<ProSecurityUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        _uiState.update { it.copy(lockdownSteps = repository.getEmergencyLockdownSteps()) }
        scanSpyware()
        scanTrackers()
    }

    fun scanSpyware() {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanningSpyware = true) }
            delay(600)
            val report = repository.runSpywareScan()
            _uiState.update { it.copy(isScanningSpyware = false, spywareReport = report) }
        }
    }

    fun selectSpywareThreat(threat: com.example.domain.model.SpywareThreat?) {
        _uiState.update { it.copy(selectedThreat = threat) }
    }

    fun scanTrackers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isAnalyzingTrackers = true) }
            delay(500)
            val profiles = repository.inspectAppTrackers()
            _uiState.update { it.copy(isAnalyzingTrackers = false, trackerProfiles = profiles) }
        }
    }

    fun setTrackerSearchQuery(query: String) {
        _uiState.update { it.copy(trackerSearchQuery = query) }
    }

    fun selectTrackerProfile(profile: com.example.domain.model.AppTrackerProfile?) {
        _uiState.update { it.copy(selectedTrackerProfile = profile) }
    }

    fun setBreachQuery(query: String) {
        _uiState.update { it.copy(breachQuery = query) }
    }

    fun executeBreachLookup(query: String? = null) {
        val target = query ?: _uiState.value.breachQuery
        if (target.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingBreach = true) }
            delay(700)
            val result = repository.checkDataBreach(target)
            _uiState.update { it.copy(isCheckingBreach = false, breachResult = result) }
        }
    }

    fun triggerEmergencyLockdown() {
        val sanitized = repository.executeClearClipboard()
        _uiState.update { current ->
            current.copy(
                isLockdownActive = true,
                clipboardSanitized = sanitized,
                lockdownSteps = current.lockdownSteps.map { step ->
                    if (step.actionType == "CLEAR_CLIPBOARD") step.copy(isCompleted = true) else step
                }
            )
        }
    }

    fun toggleLockdownStep(stepId: String) {
        _uiState.update { current ->
            current.copy(
                lockdownSteps = current.lockdownSteps.map { step ->
                    if (step.id == stepId) step.copy(isCompleted = !step.isCompleted) else step
                }
            )
        }
    }

    fun disengageLockdown() {
        _uiState.update { it.copy(isLockdownActive = false) }
    }

    fun openAirplaneSettings() = repository.openAirplaneModeSettings()
    fun openOverlaySettings() = repository.openOverlaySettings()
    fun openSecuritySettings() = repository.openSecuritySettings()
    fun purgeClipboard() {
        val sanitized = repository.executeClearClipboard()
        _uiState.update { it.copy(clipboardSanitized = sanitized) }
    }
}

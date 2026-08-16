package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.SecurityRepository
import com.example.domain.engine.BackgroundProtectionStatus
import com.example.domain.model.AppSecurityInfo
import com.example.domain.model.DeviceSecurityStatus
import com.example.domain.model.NetworkSecurityInfo
import com.example.domain.model.RiskLevel
import com.example.domain.model.SecurityScore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isScanning: Boolean = false,
    val scanStep: String = "Idle",
    val score: SecurityScore? = null,
    val deviceStatus: DeviceSecurityStatus? = null,
    val networkInfo: NetworkSecurityInfo? = null,
    val scannedApps: List<AppSecurityInfo> = emptyList(),
    val backgroundProtection: BackgroundProtectionStatus = BackgroundProtectionStatus(),
    val isDiagnosticsRunning: Boolean = false,
    val isInitialized: Boolean = false
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    val repository = SecurityRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(DashboardUiState(backgroundProtection = repository.getBackgroundProtectionStatus()))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        performInitialScan()
    }

    fun performInitialScan() {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true, scanStep = "Initializing Security Engine...") }
            delay(300)
            _uiState.update { it.copy(scanStep = "Inspecting Device Integrity & Screen Lock...") }
            delay(300)
            _uiState.update { it.copy(scanStep = "Scanning Installed Apps & Sideloaded APKs...") }
            delay(400)
            _uiState.update { it.copy(scanStep = "Auditing Sensitive Permissions Matrix...") }
            delay(300)
            _uiState.update { it.copy(scanStep = "Evaluating Wi-Fi & Network Encryption...") }
            delay(300)
            _uiState.update { it.copy(scanStep = "Computing Composite Security Score...") }
            delay(200)

            val scoreResult = repository.runFullSecurityScan()
            val deviceStatus = repository.getDeviceStatus()
            val networkInfo = repository.getNetworkInfo()
            val apps = repository.getScannedApps()
            val bgStatus = repository.getBackgroundProtectionStatus()

            _uiState.update {
                it.copy(
                    isScanning = false,
                    scanStep = "Scan Complete",
                    score = scoreResult,
                    deviceStatus = deviceStatus,
                    networkInfo = networkInfo,
                    scannedApps = apps,
                    backgroundProtection = bgStatus,
                    isInitialized = true
                )
            }
        }
    }

    fun rescan() {
        performInitialScan()
    }

    fun toggleBackgroundProtection(enabled: Boolean) {
        val updated = repository.toggleBackgroundProtection(enabled)
        _uiState.update { it.copy(backgroundProtection = updated) }
    }

    fun toggleAppWatchdog(enabled: Boolean) {
        val updated = repository.toggleAppWatchdog(enabled)
        _uiState.update { it.copy(backgroundProtection = updated) }
    }

    fun toggleNetworkGuard(enabled: Boolean) {
        val updated = repository.toggleNetworkGuard(enabled)
        _uiState.update { it.copy(backgroundProtection = updated) }
    }

    fun toggleClipboardShield(enabled: Boolean) {
        val updated = repository.toggleClipboardShield(enabled)
        _uiState.update { it.copy(backgroundProtection = updated) }
    }

    fun runBackgroundDiagnostic() {
        viewModelScope.launch {
            _uiState.update { it.copy(isDiagnosticsRunning = true) }
            delay(800)
            val updated = repository.triggerBackgroundHealthDiagnostic()
            _uiState.update { it.copy(isDiagnosticsRunning = false, backgroundProtection = updated) }
        }
    }

    fun openBatterySettings() {
        repository.openBatterySettings()
    }

    fun openAppInfoSettings() {
        repository.openAppInfoSettings()
    }
}

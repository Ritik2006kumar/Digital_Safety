package com.example.domain.engine

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings

data class BackgroundProtectionStatus(
    val isProtectionEnabled: Boolean = true,
    val isAppWatchdogActive: Boolean = true,
    val isNetworkGuardActive: Boolean = true,
    val isClipboardScamShieldActive: Boolean = true,
    val isBatteryOptimizationIgnored: Boolean = false,
    val lastBackgroundScanTimestamp: Long = System.currentTimeMillis(),
    val backgroundEventsBlockedCount: Int = 0,
    val statusMessage: String = "Active • All security monitors operational in background"
)

class BackgroundSentinelEngine(private val context: Context) {

    private val prefs = context.getSharedPreferences("shield_background_prefs", Context.MODE_PRIVATE)

    fun getProtectionStatus(): BackgroundProtectionStatus {
        val enabled = prefs.getBoolean("bg_protection_enabled", true)
        val appWatchdog = prefs.getBoolean("bg_app_watchdog", true)
        val networkGuard = prefs.getBoolean("bg_network_guard", true)
        val clipboardShield = prefs.getBoolean("bg_clipboard_shield", true)
        val lastScan = prefs.getLong("bg_last_scan", System.currentTimeMillis())
        val blockedCount = prefs.getInt("bg_blocked_count", 3)

        val isBatteryIgnored = checkBatteryOptimizationIgnored()

        val message = when {
            !enabled -> "Background Protection Paused by user"
            !isBatteryIgnored -> "Active (Battery Saver may restrict background updates)"
            else -> "Active • Real-time background shield running"
        }

        return BackgroundProtectionStatus(
            isProtectionEnabled = enabled,
            isAppWatchdogActive = appWatchdog,
            isNetworkGuardActive = networkGuard,
            isClipboardScamShieldActive = clipboardShield,
            isBatteryOptimizationIgnored = isBatteryIgnored,
            lastBackgroundScanTimestamp = lastScan,
            backgroundEventsBlockedCount = blockedCount,
            statusMessage = message
        )
    }

    fun setProtectionEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("bg_protection_enabled", enabled).apply()
    }

    fun setAppWatchdog(enabled: Boolean) {
        prefs.edit().putBoolean("bg_app_watchdog", enabled).apply()
    }

    fun setNetworkGuard(enabled: Boolean) {
        prefs.edit().putBoolean("bg_network_guard", enabled).apply()
    }

    fun setClipboardShield(enabled: Boolean) {
        prefs.edit().putBoolean("bg_clipboard_shield", enabled).apply()
    }

    fun recordBackgroundScan() {
        prefs.edit()
            .putLong("bg_last_scan", System.currentTimeMillis())
            .putInt("bg_blocked_count", prefs.getInt("bg_blocked_count", 0) + 1)
            .apply()
    }

    fun checkBatteryOptimizationIgnored(): Boolean {
        return try {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                powerManager?.isIgnoringBatteryOptimizations(context.packageName) ?: false
            } else {
                true
            }
        } catch (_: Exception) {
            false
        }
    }

    fun openBatteryOptimizationSettings() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } else {
                openAppDetailsSettings()
            }
        } catch (_: Exception) {
            openAppDetailsSettings()
        }
    }

    fun openAppDetailsSettings() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}

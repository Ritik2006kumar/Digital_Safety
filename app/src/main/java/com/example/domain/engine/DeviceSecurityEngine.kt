package com.example.domain.engine

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.provider.Settings
import com.example.domain.model.DeviceSecurityStatus
import java.io.File

class DeviceSecurityEngine(private val context: Context) {

    fun inspectDeviceSecurity(): DeviceSecurityStatus {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        val isDeviceSecure = keyguardManager?.isDeviceSecure ?: false

        // Check Developer Options
        val devOptions = try {
            Settings.Global.getInt(context.contentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) != 0
        } catch (_: Exception) {
            false
        }

        // Check ADB / USB Debugging
        val adbEnabled = try {
            Settings.Global.getInt(context.contentResolver, Settings.Global.ADB_ENABLED, 0) != 0
        } catch (_: Exception) {
            false
        }

        // Root Detection Heuristics
        val isRooted = checkRootHeuristics()

        // Unknown Sources / Install non-market apps (legacy check or modern state)
        val unknownSources = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // In modern Android, it's per-app; we flag false by default unless special indicator
                false
            } else {
                @Suppress("DEPRECATION")
                Settings.Secure.getInt(context.contentResolver, Settings.Secure.INSTALL_NON_MARKET_APPS, 0) != 0
            }
        } catch (_: Exception) {
            false
        }

        var score = 20
        if (!isDeviceSecure) score -= 8 // High penalty for no screen lock
        if (adbEnabled) score -= 4 // Moderate penalty for USB debugging enabled
        if (devOptions) score -= 2 // Slight note for developer options
        if (isRooted) score -= 8 // Major penalty for rooted system integrity
        if (unknownSources) score -= 3

        if (score < 0) score = 0

        val patch = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Build.VERSION.SECURITY_PATCH
        } else {
            "N/A"
        }

        return DeviceSecurityStatus(
            isDeviceSecure = isDeviceSecure,
            isDeveloperOptionsEnabled = devOptions,
            isAdbEnabled = adbEnabled,
            isRootDetected = isRooted,
            isUnknownSourcesAllowed = unknownSources,
            androidVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
            securityPatchLevel = patch,
            deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}",
            score = score
        )
    }

    private fun checkRootHeuristics(): Boolean {
        val buildTags = Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true
        }

        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        for (path in paths) {
            if (File(path).exists()) return true
        }
        return false
    }
}

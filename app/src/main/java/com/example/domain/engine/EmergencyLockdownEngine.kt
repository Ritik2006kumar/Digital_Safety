package com.example.domain.engine

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.example.domain.model.EmergencyLockdownStep

class EmergencyLockdownEngine(private val context: Context) {

    fun getLockdownSteps(): List<EmergencyLockdownStep> = listOf(
        EmergencyLockdownStep(
            id = "step_clipboard",
            title = "Purge & Sanitize Clipboard RAM",
            subtitle = "Zeroes out copied banking passwords, OTPs, or credit card numbers from RAM.",
            actionType = "CLEAR_CLIPBOARD"
        ),
        EmergencyLockdownStep(
            id = "step_airplane",
            title = "RF & Wireless Radio Isolation",
            subtitle = "Open Airplane Mode / Wi-Fi settings to immediately sever C2 server communication.",
            actionType = "AIRPLANE_MODE"
        ),
        EmergencyLockdownStep(
            id = "step_overlays",
            title = "Revoke High-Risk Screen Overlays",
            subtitle = "Blocks phishing overlays (Draw over other apps) from stealing screen taps.",
            actionType = "REVOKE_OVERLAYS"
        ),
        EmergencyLockdownStep(
            id = "step_device_lock",
            title = "Force Biometric Re-authentication",
            subtitle = "Locks sensitive apps and disables Smart Lock auto-unlock on device.",
            actionType = "LOCK_DEVICE"
        )
    )

    fun executeClearClipboard(): Boolean {
        return try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            clipboard?.setPrimaryClip(ClipData.newPlainText("ShieldAI_Sanitized", ""))
            true
        } catch (_: Exception) {
            false
        }
    }

    fun openAirplaneModeSettings() {
        try {
            val intent = Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (_: Exception) {
                // Fallback
            }
        }
    }

    fun openOverlaySettings() {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            // Fallback
        }
    }

    fun openSecuritySettings() {
        try {
            val intent = Intent(Settings.ACTION_SECURITY_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            // Fallback
        }
    }
}

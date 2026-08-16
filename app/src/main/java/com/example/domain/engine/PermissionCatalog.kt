package com.example.domain.engine

import android.Manifest
import com.example.domain.model.PermissionDetail
import com.example.domain.model.PermissionRiskCategory
import com.example.domain.model.RiskLevel

object PermissionCatalog {

    data class PermissionMetadata(
        val simpleName: String,
        val riskLevel: RiskLevel,
        val category: PermissionRiskCategory,
        val description: String,
        val potentialAbuse: String
    )

    private val permissionMap = mapOf(
        Manifest.permission.CAMERA to PermissionMetadata(
            simpleName = "Camera Access",
            riskLevel = RiskLevel.HIGH,
            category = PermissionRiskCategory.CRITICAL_PRIVACY,
            description = "Allows the app to take photos and record videos at any time.",
            potentialAbuse = "Unauthorized photo capture or continuous video surveillance."
        ),
        Manifest.permission.RECORD_AUDIO to PermissionMetadata(
            simpleName = "Microphone Access",
            riskLevel = RiskLevel.HIGH,
            category = PermissionRiskCategory.CRITICAL_PRIVACY,
            description = "Allows the app to record audio and eavesdrop on surroundings.",
            potentialAbuse = "Secret room listening, recording conversations or ambient sounds."
        ),
        Manifest.permission.READ_CONTACTS to PermissionMetadata(
            simpleName = "Read Contacts",
            riskLevel = RiskLevel.HIGH,
            category = PermissionRiskCategory.CRITICAL_PRIVACY,
            description = "Allows the app to read all saved contacts, emails, and phone numbers.",
            potentialAbuse = "Harvesting personal contact books for spam or data brokering."
        ),
        Manifest.permission.WRITE_CONTACTS to PermissionMetadata(
            simpleName = "Modify Contacts",
            riskLevel = RiskLevel.HIGH,
            category = PermissionRiskCategory.CRITICAL_PRIVACY,
            description = "Allows the app to modify or delete contacts stored on the device.",
            potentialAbuse = "Tampering with contact info or injecting malicious numbers."
        ),
        Manifest.permission.READ_SMS to PermissionMetadata(
            simpleName = "Read SMS Messages",
            riskLevel = RiskLevel.CRITICAL,
            category = PermissionRiskCategory.CRITICAL_PRIVACY,
            description = "Allows reading all incoming and saved SMS messages.",
            potentialAbuse = "Interception of One-Time Passwords (OTPs) and banking verification codes."
        ),
        Manifest.permission.SEND_SMS to PermissionMetadata(
            simpleName = "Send SMS Messages",
            riskLevel = RiskLevel.CRITICAL,
            category = PermissionRiskCategory.CRITICAL_PRIVACY,
            description = "Allows sending SMS without user confirmation.",
            potentialAbuse = "Subscribing to premium SMS fraud or sending spam to your contacts."
        ),
        Manifest.permission.RECEIVE_SMS to PermissionMetadata(
            simpleName = "Receive SMS",
            riskLevel = RiskLevel.CRITICAL,
            category = PermissionRiskCategory.CRITICAL_PRIVACY,
            description = "Allows intercepting SMS messages as soon as they arrive.",
            potentialAbuse = "Real-time OTP theft before user reads the security notification."
        ),
        Manifest.permission.ACCESS_FINE_LOCATION to PermissionMetadata(
            simpleName = "Precise GPS Location",
            riskLevel = RiskLevel.HIGH,
            category = PermissionRiskCategory.LOCATION_TRACKING,
            description = "Allows exact GPS coordinate pinpointing of device position.",
            potentialAbuse = "Real-time physical stalking or precise profiling of daily routines."
        ),
        Manifest.permission.ACCESS_COARSE_LOCATION to PermissionMetadata(
            simpleName = "Approximate Location",
            riskLevel = RiskLevel.MEDIUM,
            category = PermissionRiskCategory.LOCATION_TRACKING,
            description = "Allows determining general city/neighborhood location via network.",
            potentialAbuse = "Location-targeted profiling and regional tracking."
        ),
        Manifest.permission.READ_CALL_LOG to PermissionMetadata(
            simpleName = "Read Call History",
            riskLevel = RiskLevel.CRITICAL,
            category = PermissionRiskCategory.CRITICAL_PRIVACY,
            description = "Allows reading detailed incoming, outgoing, and missed call logs.",
            potentialAbuse = "Social graph surveillance, tracking who you speak with and when."
        ),
        Manifest.permission.READ_PHONE_STATE to PermissionMetadata(
            simpleName = "Phone State & Device ID",
            riskLevel = RiskLevel.MEDIUM,
            category = PermissionRiskCategory.CONNECTIVITY_DEVICE,
            description = "Allows reading phone numbers, cellular network info, and ongoing calls.",
            potentialAbuse = "Persistent device fingerprinting and cross-app tracking."
        ),
        Manifest.permission.READ_EXTERNAL_STORAGE to PermissionMetadata(
            simpleName = "Read Device Storage",
            riskLevel = RiskLevel.MEDIUM,
            category = PermissionRiskCategory.STORAGE_MEDIA,
            description = "Allows reading files, pictures, and downloads stored on device.",
            potentialAbuse = "Unauthorized scanning of sensitive documents or personal photos."
        ),
        Manifest.permission.WRITE_EXTERNAL_STORAGE to PermissionMetadata(
            simpleName = "Write Device Storage",
            riskLevel = RiskLevel.MEDIUM,
            category = PermissionRiskCategory.STORAGE_MEDIA,
            description = "Allows creating or modifying files on device storage.",
            potentialAbuse = "Ransomware-style modification or storing malicious payload files."
        ),
        Manifest.permission.POST_NOTIFICATIONS to PermissionMetadata(
            simpleName = "Post Notifications",
            riskLevel = RiskLevel.LOW,
            category = PermissionRiskCategory.NORMAL,
            description = "Allows displaying system notifications and alerts.",
            potentialAbuse = "Spamming aggressive ads or misleading phishing alerts."
        ),
        Manifest.permission.INTERNET to PermissionMetadata(
            simpleName = "Full Internet Access",
            riskLevel = RiskLevel.LOW,
            category = PermissionRiskCategory.NORMAL,
            description = "Allows transmitting data over the internet.",
            potentialAbuse = "Exfiltrating harvested device data to remote servers."
        )
    )

    fun getDetail(permissionName: String, isGranted: Boolean): PermissionDetail {
        val meta = permissionMap[permissionName]
        return if (meta != null) {
            PermissionDetail(
                name = permissionName,
                simpleName = meta.simpleName,
                isGranted = isGranted,
                riskLevel = meta.riskLevel,
                category = meta.category,
                description = meta.description,
                potentialAbuse = meta.potentialAbuse
            )
        } else {
            val shortName = permissionName.substringAfterLast(".").replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
            PermissionDetail(
                name = permissionName,
                simpleName = shortName,
                isGranted = isGranted,
                riskLevel = RiskLevel.LOW,
                category = PermissionRiskCategory.NORMAL,
                description = "Standard application permission: $permissionName",
                potentialAbuse = "General background operation access."
            )
        }
    }
}

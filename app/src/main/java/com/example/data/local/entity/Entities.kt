package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "installed_apps")
data class InstalledAppEntity(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val versionName: String,
    val versionCode: Long,
    val targetSdk: Int,
    val installSource: String,
    val isSystemApp: Boolean,
    val requestedPermissionsCount: Int,
    val grantedPermissionsCount: Int,
    val grantedPermissionsJson: String,
    val riskScore: Int,
    val riskLevel: String,
    val riskReasonsJson: String,
    val lastScanned: Long
)

@Entity(tableName = "security_history")
data class SecurityHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val totalScore: Int,
    val deviceScore: Int,
    val appScore: Int,
    val permissionScore: Int,
    val networkScore: Int,
    val accountScore: Int,
    val issueCount: Int,
    val scannedAppsCount: Int,
    val riskyAppsCount: Int,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "vault_items")
data class VaultItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String,
    val encryptedContent: String,
    val note: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "scam_checks")
data class ScamCheckEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val isUrl: Boolean,
    val scamScore: Int,
    val riskLevel: String,
    val scamType: String,
    val indicatorsJson: String,
    val explanation: String,
    val analyzedAt: Long = System.currentTimeMillis()
)

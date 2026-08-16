package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.InstalledAppEntity
import com.example.data.local.entity.ScamCheckEntity
import com.example.data.local.entity.SecurityHistoryEntity
import com.example.data.local.entity.VaultItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InstalledAppDao {
    @Query("SELECT * FROM installed_apps ORDER BY riskScore DESC, appName ASC")
    fun getAllApps(): Flow<List<InstalledAppEntity>>

    @Query("SELECT * FROM installed_apps WHERE riskLevel IN ('CRITICAL', 'HIGH') ORDER BY riskScore DESC")
    fun getHighRiskApps(): Flow<List<InstalledAppEntity>>

    @Query("SELECT * FROM installed_apps WHERE packageName = :packageName LIMIT 1")
    suspend fun getAppByPackage(packageName: String): InstalledAppEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApps(apps: List<InstalledAppEntity>)

    @Query("DELETE FROM installed_apps")
    suspend fun clearAll()
}

@Dao
interface SecurityHistoryDao {
    @Query("SELECT * FROM security_history ORDER BY createdAt DESC")
    fun getAllHistory(): Flow<List<SecurityHistoryEntity>>

    @Query("SELECT * FROM security_history ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestScore(): SecurityHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: SecurityHistoryEntity): Long

    @Query("DELETE FROM security_history")
    suspend fun clearHistory()
}

@Dao
interface VaultItemDao {
    @Query("SELECT * FROM vault_items ORDER BY updatedAt DESC")
    fun getAllVaultItems(): Flow<List<VaultItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: VaultItemEntity): Long

    @Update
    suspend fun updateItem(item: VaultItemEntity)

    @Query("DELETE FROM vault_items WHERE id = :id")
    suspend fun deleteItem(id: Long)
}

@Dao
interface ScamCheckDao {
    @Query("SELECT * FROM scam_checks ORDER BY analyzedAt DESC")
    fun getAllScamChecks(): Flow<List<ScamCheckEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScamCheck(check: ScamCheckEntity): Long

    @Query("DELETE FROM scam_checks")
    suspend fun clearChecks()
}

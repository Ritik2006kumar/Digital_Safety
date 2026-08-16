package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.InstalledAppDao
import com.example.data.local.dao.ScamCheckDao
import com.example.data.local.dao.SecurityHistoryDao
import com.example.data.local.dao.VaultItemDao
import com.example.data.local.entity.InstalledAppEntity
import com.example.data.local.entity.ScamCheckEntity
import com.example.data.local.entity.SecurityHistoryEntity
import com.example.data.local.entity.VaultItemEntity

@Database(
    entities = [
        InstalledAppEntity::class,
        SecurityHistoryEntity::class,
        VaultItemEntity::class,
        ScamCheckEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun installedAppDao(): InstalledAppDao
    abstract fun securityHistoryDao(): SecurityHistoryDao
    abstract fun vaultItemDao(): VaultItemDao
    abstract fun scamCheckDao(): ScamCheckDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "digital_safety_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

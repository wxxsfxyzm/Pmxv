package com.carlyu.pmxv.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.carlyu.pmxv.local.room.dao.AccountDao
import com.carlyu.pmxv.local.room.entity.AccountEntity

@Database(entities = [AccountEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DATABASE_NAME = "pmxv_app_database"

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    // .addMigrations(...) // 如果有数据库升级
                    .fallbackToDestructiveMigration(true) // 开发阶段简单粗暴，发布前慎用
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

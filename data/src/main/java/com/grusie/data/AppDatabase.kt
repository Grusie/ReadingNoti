package com.grusie.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.grusie.data.dao.LocalPersonalSettingDao
import com.grusie.data.dao.LocalTotalSettingDao
import com.grusie.data.data.LocalPersonalSettingEntity
import com.grusie.data.data.LocalTotalSettingEntity
import com.grusie.data.mapper.RoomTypeConverter


@Database(
    entities = [LocalTotalSettingEntity::class, LocalPersonalSettingEntity::class],
    version = 3
)
@TypeConverters(RoomTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun localTotalSettingDao(): LocalTotalSettingDao
    abstract fun localPersonalSettingDao(): LocalPersonalSettingDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE total_setting ADD COLUMN type TEXT NOT NULL DEFAULT 'general'")
                db.execSQL("ALTER TABLE personal_setting ADD COLUMN type TEXT NOT NULL DEFAULT 'general'")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE total_setting ADD COLUMN imageUrl TEXT")
            }
        }
    }
}
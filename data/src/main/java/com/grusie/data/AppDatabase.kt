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
    version = 6
)
@TypeConverters(RoomTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun localTotalSettingDao(): LocalTotalSettingDao
    abstract fun localPersonalSettingDao(): LocalPersonalSettingDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE total_setting ADD COLUMN type TEXT NOT NULL DEFAULT 'GENERAL'")
                db.execSQL("ALTER TABLE personal_setting ADD COLUMN type TEXT NOT NULL DEFAULT 'GENERAL'")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE total_setting ADD COLUMN imageUrl TEXT")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE total_setting ADD COLUMN packageName TEXT")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE total_setting ADD COLUMN docName TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE total_setting ADD COLUMN isTintUse INTEGER Not NULL DEFAULT 0")
            }
        }
    }
}
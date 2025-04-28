package com.grusie.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.grusie.data.dao.LocalTotalSettingDao
import com.grusie.data.data.LocalTotalSettingEntity


@Database(
    entities = [LocalTotalSettingEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun localTotalSettingDao(): LocalTotalSettingDao
}
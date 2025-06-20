package com.grusie.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.grusie.data.dao.LocalMsgDao
import com.grusie.data.dao.LocalPersonalSettingDao
import com.grusie.data.dao.LocalTotalSettingDao
import com.grusie.data.data.LocalMsgEntity
import com.grusie.data.data.LocalPersonalSettingEntity
import com.grusie.data.data.LocalTotalSettingEntity
import com.grusie.data.mapper.RoomTypeConverter


@Database(
    entities = [LocalTotalSettingEntity::class, LocalPersonalSettingEntity::class, LocalMsgEntity::class],
    version = 1
)
@TypeConverters(RoomTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun localTotalSettingDao(): LocalTotalSettingDao
    abstract fun localPersonalSettingDao(): LocalPersonalSettingDao
    abstract fun localMsgDao(): LocalMsgDao

    companion object {}
}
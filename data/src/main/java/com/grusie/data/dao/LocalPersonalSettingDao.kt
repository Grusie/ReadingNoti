package com.grusie.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.grusie.data.data.LocalPersonalSettingEntity


@Dao
interface LocalPersonalSettingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalPersonalSettingList(localPersonalSettingList: List<LocalPersonalSettingEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalPersonalSetting(localPersonalSettingEntity: LocalPersonalSettingEntity)

    @Query("SELECT * FROM personal_setting")
    suspend fun getLocalPersonalSettingList(): List<LocalPersonalSettingEntity>

    @Query("DELETE From personal_setting")
    suspend fun deleteLocalPersonalSettingList()
}
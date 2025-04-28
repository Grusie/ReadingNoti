package com.grusie.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.grusie.data.data.LocalTotalSettingEntity


@Dao
interface LocalTotalSettingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalTotalSettingList(localTotalSettingEntity: List<LocalTotalSettingEntity>)

    @Query("SELECT * FROM total_setting")
    suspend fun getLocalTotalSettingList(): List<LocalTotalSettingEntity>

    @Query("DELETE From total_setting")
    suspend fun deleteLocalTotalSettingList()
}
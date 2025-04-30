package com.grusie.data.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.grusie.core.common.SettingType

@Entity(tableName = "personal_setting")
data class LocalPersonalSettingEntity(
    @PrimaryKey val menuId: Int = -1,
    val type: SettingType,
    val isEnabled: Boolean = false,
    val customData: String? = null
)
package com.grusie.data.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personal_setting")
data class LocalPersonalSettingEntity(
    @PrimaryKey val menuId: Int = -1,
    val isEnabled: Boolean = false,
    val customData: String? = null
)
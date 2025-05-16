package com.grusie.data.mapper

import androidx.room.TypeConverter
import com.grusie.core.common.SettingType

class RoomTypeConverter {
    @TypeConverter
    fun fromSettingType(type: SettingType): String = type.name

    @TypeConverter
    fun toSettingType(value: String): SettingType = SettingType.from(value)
}
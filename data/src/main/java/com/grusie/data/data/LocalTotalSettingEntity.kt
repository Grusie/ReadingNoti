package com.grusie.data.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 로컬에 저장할 전체 설정 Data Class
 * 인터넷 연결이 되지 않은 상태에서도 앱을 원할하게 사용하기 위해 사용
 */
@Entity(tableName = "total_setting")
data class LocalTotalSettingEntity(
    @PrimaryKey val menuId: Int,
    val isVisible: Boolean,
    val displayName: String,
    val isInitEnabled: Boolean,
    val description: String,
)
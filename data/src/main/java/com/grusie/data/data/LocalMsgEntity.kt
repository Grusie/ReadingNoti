package com.grusie.data.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 받아온 알림을 RoomDB에 저장하기 위한 Entity
 */
@Entity(
    tableName = "message_table",
    indices = [Index(value = ["menuId"])]
)
data class LocalMsgEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val menuId: Int,
    val title: String,
    val subTitle: String,
    val content: String,
    val timeStamp: Long
)
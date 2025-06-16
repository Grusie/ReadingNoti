package com.grusie.domain.data

data class DomainMsgData(
    val id: Long? = null,
    val menuId: Int,
    val title: String,
    val subTitle:String,
    val content: String,
    val timeStamp: Long
)
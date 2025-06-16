package com.grusie.data.mapper

import com.grusie.data.data.LocalMsgEntity
import com.grusie.domain.data.DomainMsgData

fun LocalMsgEntity.toDomain(): DomainMsgData {
    return DomainMsgData(
        id = this.id,
        menuId = this.menuId,
        title = this.title,
        subTitle = this.subTitle,
        content = this.content,
        timeStamp = this.timeStamp
    )
}

fun DomainMsgData.toEntity(): LocalMsgEntity {
    return LocalMsgEntity(
        menuId = this.menuId,
        title = this.title,
        subTitle = this.subTitle,
        content = this.content,
        timeStamp = this.timeStamp
    )
}
package com.grusie.data.mapper

import com.grusie.data.data.UserDto
import com.grusie.domain.data.DomainUserDto

fun UserDto.toDomain(): DomainUserDto {
    return DomainUserDto(
        uid = this.uid,
        name = this.name,
        email = this.email,
        isAdmin = this.isAdmin
    )
}
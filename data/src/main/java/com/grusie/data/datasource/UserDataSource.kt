package com.grusie.data.datasource

import com.grusie.data.data.UserDto
import com.grusie.domain.data.DomainUserDto

interface UserDataSource {
    suspend fun getUserList(): Result<List<UserDto>>
    suspend fun isAdmin(uid: String): Result<Boolean>
    suspend fun setAdmin(uid: String, isAdmin: Boolean): Result<Unit>
    suspend fun initUser(domainUserDto: DomainUserDto): Result<Unit>
}
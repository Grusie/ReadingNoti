package com.grusie.domain.repository

import com.grusie.domain.data.DomainUserDto

interface UserRepository {
    suspend fun getUserList(): Result<List<DomainUserDto>>
    suspend fun isAdmin(uid: String): Result<Boolean>
    suspend fun setAdmin(uid: String, isAdmin: Boolean): Result<Unit>
    suspend fun initUser(domainUserDto: DomainUserDto): Result<Unit>
}
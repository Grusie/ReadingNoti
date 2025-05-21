package com.grusie.data.repositoryImpl

import com.grusie.data.datasource.UserDataSource
import com.grusie.data.mapper.toDomain
import com.grusie.domain.data.DomainUserDto
import com.grusie.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource
) : UserRepository {
    override suspend fun getUserList(): Result<List<DomainUserDto>> {
        return userDataSource.getUserList().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun isAdmin(uid: String): Result<Boolean> {
        return userDataSource.isAdmin(uid)
    }

    override suspend fun setAdmin(uid: String, isAdmin: Boolean): Result<Unit> {
        return userDataSource.setAdmin(uid, isAdmin)
    }

    override suspend fun initUser(domainUserDto: DomainUserDto): Result<Unit> {
        return userDataSource.initUser(domainUserDto)
    }
}
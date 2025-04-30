package com.grusie.data.repositoryImpl

import com.grusie.data.datasource.AdminDataSource
import com.grusie.data.mapper.toDomain
import com.grusie.domain.data.DomainUserDto
import com.grusie.domain.repository.AdminRepository
import javax.inject.Inject

class AdminRepositoryImpl @Inject constructor(
    private val adminDataSource: AdminDataSource
) : AdminRepository {
    override suspend fun getAdminList(): Result<List<DomainUserDto>> {
        return adminDataSource.getAdminList().map { list -> list.map { it.toDomain() } }
    }
}
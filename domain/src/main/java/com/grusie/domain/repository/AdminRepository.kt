package com.grusie.domain.repository

import com.grusie.domain.data.DomainUserDto

interface AdminRepository {
    suspend fun getAdminList(): Result<List<DomainUserDto>>
}
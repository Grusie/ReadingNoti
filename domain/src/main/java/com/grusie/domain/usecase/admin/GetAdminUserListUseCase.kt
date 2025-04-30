package com.grusie.domain.usecase.admin

import com.grusie.domain.data.DomainUserDto
import com.grusie.domain.repository.AdminRepository

class GetAdminUserListUseCase(
    private val repository: AdminRepository
) {
    suspend operator fun invoke(): Result<List<DomainUserDto>> {
        return repository.getAdminList()
    }
}
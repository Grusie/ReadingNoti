package com.grusie.domain.usecase.user

import com.grusie.domain.data.DomainUserDto
import com.grusie.domain.repository.UserRepository

class GetUserListUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Result<List<DomainUserDto>> {
        return repository.getUserList()
    }
}
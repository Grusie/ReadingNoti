package com.grusie.domain.usecase.user

import com.grusie.domain.repository.UserRepository

class IsAdminUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(uid: String): Result<Boolean> {
        return repository.isAdmin(uid)
    }
}
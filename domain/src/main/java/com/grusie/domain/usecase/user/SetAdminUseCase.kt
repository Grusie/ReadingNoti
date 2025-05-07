package com.grusie.domain.usecase.user

import com.grusie.domain.repository.UserRepository

class SetAdminUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(uid: String, isAdmin: Boolean): Result<Unit> {
        return repository.setAdmin(uid, isAdmin)
    }
}
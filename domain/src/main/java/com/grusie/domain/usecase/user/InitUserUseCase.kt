package com.grusie.domain.usecase.user

import com.grusie.domain.data.DomainUserDto
import com.grusie.domain.repository.UserRepository
import javax.inject.Inject

/**
 * 전체 설정을 삭제 할 수 있음(APP 타입만 사용)
 * 다중 처리를 위해 list로 받도록 처리
 */
class InitUserUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(domainUserDto: DomainUserDto): Result<Unit> {
        return repository.initUser(domainUserDto)
    }
}
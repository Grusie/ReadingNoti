package com.grusie.domain.usecase.user

data class UserUseCases(
    val getUserListUseCase: GetUserListUseCase,
    val isAdminUseCase: IsAdminUseCase,
    val setAdminUseCase: SetAdminUseCase,
    val initUserUseCase: InitUserUseCase
)
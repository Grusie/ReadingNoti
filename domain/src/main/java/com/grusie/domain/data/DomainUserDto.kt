package com.grusie.domain.data

data class DomainUserDto(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val isAdmin: Boolean = false
)
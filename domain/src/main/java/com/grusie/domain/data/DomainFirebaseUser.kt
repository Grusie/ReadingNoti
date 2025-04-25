package com.grusie.domain.data

data class DomainFirebaseUser(
    val uid:String,
    val email: String? = null,
    val name: String? = null
)
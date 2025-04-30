package com.grusie.data.datasource

import com.grusie.data.data.UserDto

interface AdminDataSource {
    suspend fun getAdminList(): Result<List<UserDto>>
}
package com.grusie.domain.repository

interface StorageRepository {
    suspend fun uploadFileToStorage(
        bucketName: String,
        prePath: String?,
        path: String,
        bytes: ByteArray
    ): Result<String>
}
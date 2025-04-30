package com.grusie.domain.repository

interface StorageRepository {
    suspend fun uploadFileToStorage(
        bucketName: String,
        path: String,
        bytes: ByteArray
    ): Result<Unit>
}
package com.grusie.data.datasource

interface StorageDataSource {
    suspend fun uploadFile(bucketName: String, path: String, bytes: ByteArray): Result<Unit>
}
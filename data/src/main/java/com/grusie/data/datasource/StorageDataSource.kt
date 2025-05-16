package com.grusie.data.datasource

interface StorageDataSource {
    suspend fun uploadFile(
        bucketName: String,
        prePath: String? = null,
        path: String,
        bytes: ByteArray
    ): Result<String>
}
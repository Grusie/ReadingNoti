package com.grusie.data.repositoryImpl

import com.grusie.data.datasource.StorageDataSource
import com.grusie.domain.repository.StorageRepository
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    private val storageDataSource: StorageDataSource
) : StorageRepository {
    override suspend fun uploadFileToStorage(
        bucketName: String,
        path: String,
        bytes: ByteArray
    ): Result<Unit> {
        return storageDataSource.uploadFile(bucketName, path, bytes)
    }
}
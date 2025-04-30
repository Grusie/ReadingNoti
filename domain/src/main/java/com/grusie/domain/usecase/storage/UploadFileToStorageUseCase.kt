package com.grusie.domain.usecase.storage

import com.grusie.core.common.StorageConst
import com.grusie.domain.repository.StorageRepository

class UploadFileToStorageUseCase(
    private val repository: StorageRepository
) {
    suspend operator fun invoke(
        bucketName: String = StorageConst.APP_ICON_BUCKET_NAME,
        path: String,
        bytes: ByteArray
    ): Result<Unit> {
        return repository.uploadFileToStorage(bucketName, path, bytes)
    }
}
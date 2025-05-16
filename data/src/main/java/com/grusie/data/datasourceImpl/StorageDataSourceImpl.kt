package com.grusie.data.datasourceImpl

import com.grusie.core.common.StorageConst
import com.grusie.core.utils.NetworkChecker
import com.grusie.data.datasource.StorageDataSource
import com.grusie.domain.data.CustomException
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import javax.inject.Inject

class StorageDataSourceImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val networkChecker: NetworkChecker
) : StorageDataSource {
    override suspend fun uploadFile(
        bucketName: String,
        prePath: String?,
        path: String,
        bytes: ByteArray
    ): Result<String> {
        return try {
            if (!networkChecker.isNetworkAvailable()) throw CustomException.NetworkError

            val bucket = supabaseClient.storage.from(bucketName)

            if(!prePath.isNullOrEmpty()) {
                // 파일 존재 확인
                val fileName = prePath.substringAfterLast("/")
                val existingFiles = bucket.list {
                    search = fileName
                }

                val fileExists = existingFiles.any { it.name == fileName }

                if (fileExists) {
                    // 파일 삭제
                    bucket.delete(listOf(fileName))
                }
            }

            bucket.upload(path, bytes) { upsert = true }
            Result.success("https://${StorageConst.STORAGE_PROJECT_NAME}.supabase.co/storage/v1/object/public/$bucketName/$path")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
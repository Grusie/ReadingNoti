package com.grusie.data.datasourceImpl

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
        path: String,
        bytes: ByteArray
    ): Result<Unit> {
        return try {
            if (!networkChecker.isNetworkAvailable()) throw CustomException.NetworkError
            supabaseClient.storage.from(bucketName).upload(path, bytes) { upsert = false }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
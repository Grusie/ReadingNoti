package com.grusie.data.datasourceImpl

import com.google.firebase.firestore.FirebaseFirestore
import com.grusie.core.common.CollectionKind
import com.grusie.core.utils.NetworkChecker
import com.grusie.data.data.UserDto
import com.grusie.data.datasource.AdminDataSource
import com.grusie.domain.data.CustomException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AdminDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val networkChecker: NetworkChecker
) : AdminDataSource {
    override suspend fun getAdminList(): Result<List<UserDto>> {
        return try {
            if (!networkChecker.isNetworkAvailable()) throw CustomException.NetworkError

            val snapShot = firestore.collection(CollectionKind.ADMIN_USER_LIST).get().await()

            if (snapShot.isEmpty) {
                return Result.failure(CustomException.NotFoundOnServer)
            }
            val adminUserList = snapShot.map { doc ->
                val uid = doc.getString("uid") ?: ""
                val name = doc.getString("name") ?: ""

                UserDto(
                    uid = uid,
                    name = name
                )
            }

            return Result.success(adminUserList)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
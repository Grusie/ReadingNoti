package com.grusie.data.datasourceImpl

import com.google.firebase.firestore.FirebaseFirestore
import com.grusie.core.common.CollectionKind
import com.grusie.core.utils.NetworkChecker
import com.grusie.data.data.UserDto
import com.grusie.data.datasource.UserDataSource
import com.grusie.domain.data.CustomException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val networkChecker: NetworkChecker
) : UserDataSource {
    override suspend fun getUserList(): Result<List<UserDto>> {
        return try {
            if (!networkChecker.isNetworkAvailable()) throw CustomException.NetworkError

            val snapShot = firestore.collection(CollectionKind.USER_LIST).get().await()

            if (snapShot.isEmpty) {
                return Result.failure(CustomException.NotFoundOnServer)
            }
            val adminUserList = snapShot.map { doc ->
                val uid = doc.id
                val name = doc.getString("name") ?: "unnamed"
                val email = doc.getString("email") ?: ""
                val isAdmin = doc.getBoolean("isAdmin") ?: false

                UserDto(
                    uid = uid,
                    name = name,
                    email = email,
                    isAdmin = isAdmin
                )
            }

            return Result.success(adminUserList)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isAdmin(uid: String): Result<Boolean> {
        return try {
            if (!networkChecker.isNetworkAvailable()) throw CustomException.NetworkError

            val snapShot =
                firestore.collection(CollectionKind.USER_LIST).document(uid).get().await()

            val isAdmin = snapShot.getBoolean("isAdmin") ?: false

            return Result.success(isAdmin)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setAdmin(uid: String, isAdmin: Boolean): Result<Unit> {
        return try {
            if (!networkChecker.isNetworkAvailable()) throw CustomException.NetworkError

            firestore.collection(CollectionKind.USER_LIST).document(uid).update(
                "isAdmin", isAdmin
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
package com.grusie.data.datasourceImpl

import com.google.firebase.firestore.FirebaseFirestore
import com.grusie.core.common.CollectionKind
import com.grusie.core.common.ServerKey
import com.grusie.core.utils.NetworkChecker
import com.grusie.data.data.UserDto
import com.grusie.data.datasource.UserDataSource
import com.grusie.domain.data.CustomException
import com.grusie.domain.data.DomainUserDto
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
                val name = doc.getString(ServerKey.User.KEY_NAME) ?: "unnamed"
                val email = doc.getString(ServerKey.User.KEY_EMAIL) ?: ""
                val isAdmin = doc.getBoolean(ServerKey.User.KEY_IS_ADMIN) ?: false

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

            val isAdmin = snapShot.getBoolean(ServerKey.User.KEY_IS_ADMIN) ?: false

            return Result.success(isAdmin)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setAdmin(uid: String, isAdmin: Boolean): Result<Unit> {
        return try {
            if (!networkChecker.isNetworkAvailable()) throw CustomException.NetworkError

            firestore.collection(CollectionKind.USER_LIST).document(uid).update(
                ServerKey.User.KEY_IS_ADMIN, isAdmin
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun initUser(domainUserDto: DomainUserDto): Result<Unit> {
        return try {
            if (!networkChecker.isNetworkAvailable()) throw CustomException.NetworkError

            val collection = firestore.collection(CollectionKind.USER_LIST)

            val userItem = collection.document(domainUserDto.uid).get().await()

            if (userItem.exists()) {
                Result.success(Unit)
            } else {
                collection.document(domainUserDto.uid)
                    .set(
                        domainUserDto.let {
                            mapOf(
                                ServerKey.User.KEY_UID to it.uid,
                                ServerKey.User.KEY_IS_ADMIN to it.isAdmin,
                                ServerKey.User.KEY_EMAIL to it.email,
                                ServerKey.User.KEY_NAME to it.name
                            )
                        }
                    ).await()

                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
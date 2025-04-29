package com.grusie.data.datasourceImpl

import com.google.firebase.firestore.FirebaseFirestore
import com.grusie.core.utils.NetworkChecker
import com.grusie.data.data.CollectionKind
import com.grusie.data.data.PersonalSettingDto
import com.grusie.data.data.TotalSettingDto
import com.grusie.data.datasource.TotalSettingDataSource
import com.grusie.domain.data.CustomException
import com.grusie.domain.data.DomainPersonalSettingDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TotalSettingDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val networkChecker: NetworkChecker
) : TotalSettingDataSource {
    override suspend fun getTotalSettingList(): Result<List<TotalSettingDto>> {
        return try {
            if (!networkChecker.isNetworkAvailable()) return Result.failure(CustomException.NetworkError)

            val snapShot = firestore.collection(CollectionKind.TOTAL_SETTING_LIST).get().await()
            val totalSettingList = snapShot.documents.mapNotNull { doc ->
                val isVisible = doc.getBoolean("isVisible") ?: false
                val isInitEnabled = doc.getBoolean("isInitEnabled") ?: false
                val menuId = doc.getLong("menuId")?.toInt() ?: -1
                val displayName = doc.getString("displayName") ?: ""
                val description = doc.getString("description") ?: ""

                TotalSettingDto(
                    menuId = menuId,
                    isVisible = isVisible,
                    displayName = displayName,
                    isInitEnabled = isInitEnabled,
                    description = description
                )
            }

            Result.success(totalSettingList.sortedBy { it.menuId })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPersonalSettingList(uid: String): Result<List<PersonalSettingDto>> {
        return try {
            if (!networkChecker.isNetworkAvailable()) return Result.failure(CustomException.NetworkError)

            val snapShot = firestore.collection(CollectionKind.PERSONAL_SETTING_LIST).document(uid)
                .collection(CollectionKind.SUB_PERSONAL_SETTING_LIST).get().await()

            if (snapShot.isEmpty) {
                return Result.failure(CustomException.NotFoundOnServer)
            }
            val personalSettingList = snapShot.map { doc ->
                val menuId = doc.getLong("menuId")?.toInt() ?: -1
                val isEnabled = doc.getBoolean("isEnabled") ?: false
                val customData = doc.getString("customData")

                PersonalSettingDto(
                    menuId = menuId,
                    isEnabled = isEnabled,
                    customData = customData
                )
            }

            Result.success(personalSettingList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setPersonalSettingList(
        uid: String,
        list: List<DomainPersonalSettingDto>
    ): Result<Unit> {
        return try {
            if (!networkChecker.isNetworkAvailable()) return Result.failure(CustomException.NetworkError)
            val collectionRef = firestore
                .collection(CollectionKind.PERSONAL_SETTING_LIST)
                .document(uid)
                .collection(CollectionKind.SUB_PERSONAL_SETTING_LIST)

            list.forEach { setting ->
                collectionRef
                    .document(setting.menuId.toString())
                    .set(
                        mapOf(
                            "menuId" to setting.menuId,
                            "isEnabled" to setting.isEnabled,
                            "customData" to setting.customData
                        )
                    ).await()
            }
            return Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
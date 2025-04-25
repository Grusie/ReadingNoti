package com.grusie.data.datasourceImpl

import com.google.firebase.firestore.FirebaseFirestore
import com.grusie.data.data.CollectionKind
import com.grusie.data.data.TotalSettingDto
import com.grusie.data.datasource.TotalSettingDataSource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TotalSettingDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : TotalSettingDataSource {
    override suspend fun getTotalSettingList(): Result<List<TotalSettingDto>> {
        return try {
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

            Result.success(totalSettingList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
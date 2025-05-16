package com.grusie.data.datasourceImpl

import com.google.firebase.firestore.FirebaseFirestore
import com.grusie.core.common.CollectionKind
import com.grusie.core.common.ServerKey
import com.grusie.core.common.SettingType
import com.grusie.core.common.TotalMenu
import com.grusie.core.utils.NetworkChecker
import com.grusie.data.data.PersonalSettingDto
import com.grusie.data.data.TotalSettingDto
import com.grusie.data.datasource.TotalSettingDataSource
import com.grusie.domain.data.CustomException
import com.grusie.domain.data.DomainPersonalSettingDto
import com.grusie.domain.data.DomainTotalSettingDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TotalSettingDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val networkChecker: NetworkChecker
) : TotalSettingDataSource {
    override suspend fun getTotalSettingList(type: SettingType?): Result<List<TotalSettingDto>> {
        return try {
            if (!networkChecker.isNetworkAvailable()) return Result.failure(CustomException.NetworkError)

            val snapShot = firestore.collection(CollectionKind.TOTAL_SETTING_LIST).get().await()
            val totalSettingList = snapShot.documents.mapNotNull { doc ->
                val isVisible = doc.getBoolean(ServerKey.TotalSetting.KEY_VISIBLE) ?: false
                val isInitEnabled = doc.getBoolean(ServerKey.TotalSetting.KEY_INIT_ENABLED) ?: false
                val serverType =
                    doc.getString(ServerKey.TotalSetting.KEY_TYPE) ?: SettingType.GENERAL.name
                val menuId = doc.getLong(ServerKey.TotalSetting.KEY_MENU_ID)?.toInt() ?: -1
                val displayName = doc.getString(ServerKey.TotalSetting.KEY_DISPLAY_NAME) ?: ""
                val description = doc.getString(ServerKey.TotalSetting.KEY_DESCRIPTION) ?: ""
                val imageUrl = doc.getString(ServerKey.TotalSetting.APP.KEY_IMAGE_URL)
                val packageName = doc.getString(ServerKey.TotalSetting.APP.KEY_PACKAGE)
                val docName = doc.id


                if (type != null && type.name != serverType) {
                    return@mapNotNull null
                }

                TotalSettingDto(
                    menuId = menuId,
                    isVisible = isVisible,
                    type = SettingType.from(serverType),
                    displayName = displayName,
                    isInitEnabled = isInitEnabled,
                    description = description,
                    imageUrl = imageUrl,
                    packageName = packageName,
                    docName = docName
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
                val menuId = doc.getLong(ServerKey.PersonalSetting.KEY_MENU_ID)?.toInt() ?: -1
                val isEnabled = doc.getBoolean(ServerKey.PersonalSetting.KEY_ENABLED) ?: false
                val customData = doc.getString(ServerKey.PersonalSetting.KEY_CUSTOM_DATA)
                val type =
                    doc.getString(ServerKey.PersonalSetting.KEY_TYPE) ?: SettingType.GENERAL.name

                PersonalSettingDto(
                    menuId = menuId,
                    type = SettingType.from(type),
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
                            ServerKey.PersonalSetting.KEY_MENU_ID to setting.menuId,
                            ServerKey.PersonalSetting.KEY_TYPE to setting.type.name,
                            ServerKey.PersonalSetting.KEY_ENABLED to setting.isEnabled,
                            ServerKey.PersonalSetting.KEY_CUSTOM_DATA to setting.customData
                        )
                    ).await()
            }
            return Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setTotalSetting(
        initTotalSettingDto: DomainTotalSettingDto?,
        domainTotalSettingDto: DomainTotalSettingDto
    ): Result<Unit> {
        return try {
            if (!networkChecker.isNetworkAvailable()) throw CustomException.NetworkError

            val docName =
                if(domainTotalSettingDto.type == SettingType.GENERAL) {
                    TotalMenu.from(domainTotalSettingDto.menuId)?.name
                        ?: throw CustomException.DataMatchingError
                } else {
                    domainTotalSettingDto.docName
                }

            if (initTotalSettingDto != null) {
                val newMap = mutableMapOf<String, Any>().apply {
                    if (initTotalSettingDto.isInitEnabled != domainTotalSettingDto.isInitEnabled)
                        put(
                            ServerKey.TotalSetting.KEY_INIT_ENABLED,
                            domainTotalSettingDto.isInitEnabled
                        )
                    if (initTotalSettingDto.isVisible != domainTotalSettingDto.isVisible)
                        put(ServerKey.TotalSetting.KEY_VISIBLE, domainTotalSettingDto.isVisible)
                    if (initTotalSettingDto.displayName != domainTotalSettingDto.displayName)
                        put(
                            ServerKey.TotalSetting.KEY_DISPLAY_NAME,
                            domainTotalSettingDto.displayName
                        )
                    if (initTotalSettingDto.description != domainTotalSettingDto.description)
                        put(
                            ServerKey.TotalSetting.KEY_DESCRIPTION,
                            domainTotalSettingDto.description
                        )

                    if (initTotalSettingDto.type == SettingType.GENERAL) {
                        // 일반 설정에서만 사용하는 필드
                    } else {
                        // 앱 설정에서만 사용하는 필드
                        if (initTotalSettingDto.packageName != domainTotalSettingDto.packageName)
                            put(
                                ServerKey.TotalSetting.APP.KEY_PACKAGE,
                                domainTotalSettingDto.packageName ?: ""
                            )

                        if (initTotalSettingDto.imageUrl != domainTotalSettingDto.imageUrl)
                            put(
                                ServerKey.TotalSetting.APP.KEY_IMAGE_URL,
                                domainTotalSettingDto.imageUrl ?: ""
                            )
                    }
                }

                firestore.collection(CollectionKind.TOTAL_SETTING_LIST).document(docName)
                    .update(
                        newMap
                    ).await()
            } else {
                firestore.collection(CollectionKind.TOTAL_SETTING_LIST).document(docName)
                    .set(
                        domainTotalSettingDto
                    ).await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTotalSetting(menuId: Int, field: Map<String, Any>): Result<Unit> {
        return try {
            if (!networkChecker.isNetworkAvailable()) throw CustomException.NetworkError
            val totalMenu = TotalMenu.from(menuId) ?: throw CustomException.DataMatchingError

            firestore.collection(CollectionKind.TOTAL_SETTING_LIST).document(totalMenu.name).update(
                field
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
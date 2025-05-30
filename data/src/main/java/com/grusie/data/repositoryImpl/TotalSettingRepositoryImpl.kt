package com.grusie.data.repositoryImpl

import com.grusie.core.common.ServerKey
import com.grusie.core.common.SettingType
import com.grusie.core.utils.LogType
import com.grusie.core.utils.LoggerInterface
import com.grusie.data.data.DefaultValues
import com.grusie.data.data.LocalPersonalSettingEntity
import com.grusie.data.datasource.LocalTotalSettingDataSource
import com.grusie.data.datasource.TotalSettingDataSource
import com.grusie.data.mapper.toDomain
import com.grusie.data.mapper.toLocal
import com.grusie.domain.data.CommonException
import com.grusie.domain.data.DomainPersonalSettingDto
import com.grusie.domain.data.DomainTotalSettingDto
import com.grusie.domain.repository.TotalSettingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TotalSettingRepositoryImpl @Inject constructor(
    private val totalSettingDataSource: TotalSettingDataSource,
    private val localTotalSettingDataSource: LocalTotalSettingDataSource,
    private val logger: LoggerInterface
) : TotalSettingRepository {
    override suspend fun getServerTotalSettingList(type: SettingType?): Result<List<DomainTotalSettingDto>> {
        return try {
            totalSettingDataSource.getTotalSettingList(type)
                .map { totalSettingList ->
                    // 서버에서 가져온 TotalSettingList 중 로컬의 PersonalSettingList에 없는 것이 있을 경우 값 저장
                    val localPersonalSettingList = localTotalSettingDataSource.getPersonalSettingList().toMutableList()

                    val localPersonalSettingMap = localPersonalSettingList.associateBy { it.menuId }
                    val totalSettingMap = totalSettingList.associateBy { it.menuId }

                    val missingSettings = totalSettingList.filter { totalItem ->
                        localPersonalSettingMap[totalItem.menuId] == null
                    }

                    val deleteSettings = localPersonalSettingList.filter { deleteSettingItem ->
                        totalSettingMap[deleteSettingItem.menuId] == null
                    }

                    if(missingSettings.isNotEmpty()) {
                        missingSettings.forEach { totalItem ->
                            val defaultPersonalSetting = LocalPersonalSettingEntity(
                                menuId = totalItem.menuId,
                                isEnabled = totalItem.isInitEnabled,
                                customData = null
                            )

                            localPersonalSettingList.add(defaultPersonalSetting)
                        }
                        saveLocalPersonalSettingList(localPersonalSettingList)
                    }

                    if(deleteSettings.isNotEmpty()) {
                        deletePersonalTotalSettingList(deleteSettings.map { it.menuId })
                    }

                    totalSettingList.map { it.toDomain() }
                }
        } catch (e: Exception) {
            logger.log(
                LogType.LOG_TYPE_E,
                this@TotalSettingRepositoryImpl::class.java.simpleName,
                "${e.message}"
            )
            return Result.failure(e)
        }
    }

    override suspend fun initTotalSettingListUseCase() {
        try {
            val result = totalSettingDataSource.getTotalSettingList()

            result.map { list ->
                // 서버에서 가져온 데이터를 Room DB에 저장
                saveLocalTotalSettingList(list.map { it.toDomain() })
            }.getOrElse { e ->
                // 서버 통신 실패 시 예외를 던짐
                throw e
            }
        } catch (e: Exception) {
            logger.log(
                LogType.LOG_TYPE_E,
                this@TotalSettingRepositoryImpl::class.java.simpleName,
                "${e.message}"
            )
            // 로컬DB에 전체 설정에 관한 정보가 있으면 냅두고, 그렇지 않다면 기본 값을 설정
            val localData = localTotalSettingDataSource.getTotalSettingList()
            if (localData.isEmpty()) {
                saveLocalTotalSettingList(DefaultValues.initLocalTotalSettingList.map { it.toDomain() })
            }
        }
    }

    override suspend fun getLocalTotalSettingList(): List<DomainTotalSettingDto> {
        return localTotalSettingDataSource.getTotalSettingList().map { it.toDomain() }
    }

    override suspend fun initPersonalSetting(uid: String?) {
        val defaultPersonalSettingList = getLocalTotalSettingList().map {
            LocalPersonalSettingEntity(
                menuId = it.menuId,
                isEnabled = it.isInitEnabled,
                customData = null
            )
        }

        try {
            val localData = localTotalSettingDataSource.getPersonalSettingList().toMutableList()

            if (uid != null) {
                if (localData.isEmpty()) {
                    // 최초 로그인이거나 로컬 데이터가 날아갔거나
                    // 서버에 있는 해당 로그인 계정의 개인 설정을 로컬에 저장
                    val result = totalSettingDataSource.getPersonalSettingList(uid)

                    result.map { list ->
                        // 서버에서 가져온 데이터를 Room DB에 저장
                        saveLocalPersonalSettingList(list.map { it.toLocal() })
                    }.getOrElse { e ->
                        // 서버 통신 실패 시 예외를 던짐
                        throw e
                    }
                } else {
                    // Empty가 아닐 경우는 서버에 로컬 데이터를 전송 <- 중간에 네트워크가 끊키거나 해서 로컬에만 적용 되어 있을 수 있기에.
                    setPersonalSettingList(
                        uid,
                        localData.map { it.toDomain() }
                    )
                }
            } else {
                // 로그인 상태가 아닐 경우는 로컬 데이터가 비어있을 경우에만 기본 세팅값 지정
                if (localData.isEmpty()) {
                    saveLocalPersonalSettingList(defaultPersonalSettingList)
                }
            }
        } catch (e: Exception) {
            logger.log(
                LogType.LOG_TYPE_E,
                this@TotalSettingRepositoryImpl::class.java.simpleName,
                "${e.message}"
            )

            when (e) {
                is CommonException.NotFoundOnServer -> {
                    // 로컬DB에 값이 없고 서버에도 값이 없을 경우는 기본 세팅 값 지정
                    saveLocalPersonalSettingList(defaultPersonalSettingList)
                }

                else -> {
                    throw e
                }
            }
        }
    }

    override suspend fun getLocalPersonalSettingList(): List<DomainPersonalSettingDto> {
        return localTotalSettingDataSource.getPersonalSettingList().map { it.toDomain() }
    }

    override suspend fun changeSettingInfo(
        uid: String?,
        domainPersonalSettingDto: DomainPersonalSettingDto
    ) {
        localTotalSettingDataSource.changePersonalSetting(domainPersonalSettingDto.toLocal())
        uid?.let {
            setPersonalSettingList(uid, listOf(domainPersonalSettingDto))
        }
    }

    override suspend fun getPersonalSettingList(uid: String): Result<List<DomainPersonalSettingDto>> {
        return try {
            totalSettingDataSource.getPersonalSettingList(uid)
                .map { list -> list.map { it.toDomain() } }
        } catch (e: Exception) {
            logger.log(
                LogType.LOG_TYPE_E,
                this@TotalSettingRepositoryImpl::class.java.simpleName,
                "${e.message}"
            )
            return Result.failure(e)
        }
    }

    override suspend fun setPersonalSettingList(
        uid: String,
        domainPersonalSettingList: List<DomainPersonalSettingDto>
    ): Result<Unit> {
        return totalSettingDataSource.setPersonalSettingList(uid, domainPersonalSettingList)
    }

    override suspend fun setLocalPersonalSettingList(domainPersonalSettingList: List<DomainPersonalSettingDto>) {
        localTotalSettingDataSource.savePersonalSettingList(domainPersonalSettingList.map { it.toLocal() })
    }

    override suspend fun updateTotalSettingVisibility(
        menuId: Int,
        isVisible: Boolean
    ): Result<Unit> {
        return totalSettingDataSource.updateTotalSetting(
            menuId,
            field = mutableMapOf(ServerKey.TotalSetting.KEY_VISIBLE to isVisible).apply {
                if (!isVisible) put(ServerKey.TotalSetting.KEY_INIT_ENABLED, false)
            })
    }

    override suspend fun setTotalSetting(
        initTotalSettingDto: DomainTotalSettingDto?,
        domainTotalSettingDto: DomainTotalSettingDto
    ): Result<Unit> {
        return totalSettingDataSource.setTotalSetting(initTotalSettingDto, domainTotalSettingDto)
    }

    override suspend fun saveLocalTotalSettingList(domainTotalSettingList: List<DomainTotalSettingDto>) {
        localTotalSettingDataSource.saveTotalSettingList(domainTotalSettingList.map { it.toLocal() })
    }

    private suspend fun deleteLocalTotalSettingList() {
        localTotalSettingDataSource.deleteTotalSettingList()
    }

    private suspend fun deletePersonalTotalSettingList(list: List<Int>) {
        localTotalSettingDataSource.deletePersonalSettingList(list)
    }

    private suspend fun saveLocalPersonalSettingList(localPersonalSettingList: List<LocalPersonalSettingEntity>) {
        localTotalSettingDataSource.savePersonalSettingList(localPersonalSettingList)
    }

    override suspend fun deleteTotalSettingList(domainTotalSettingDocNameList: List<String>): Result<Unit> {
        return totalSettingDataSource.deleteTotalSettingList(domainTotalSettingDocNameList)
    }

    override suspend fun observeTotalSettings(): Flow<List<DomainTotalSettingDto>> {
        return localTotalSettingDataSource.observeTotalSettings().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun observePersonalSettings(): Flow<List<DomainPersonalSettingDto>> {
        return localTotalSettingDataSource.observePersonalSettings().map { list -> list.map { it.toDomain() } }
    }
}
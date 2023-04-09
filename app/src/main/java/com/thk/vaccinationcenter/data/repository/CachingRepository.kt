
package com.thk.vaccinationcenter.data.repository

import com.thk.vaccinationcenter.data.local.CentersDao
import com.thk.vaccinationcenter.data.local.VaccinationCenterDatabase
import com.thk.vaccinationcenter.data.remote.CentersApiInterface
import com.thk.vaccinationcenter.data.utils.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject

interface CachingRepository {
    /**
     * 서버 API 호출하여 데이터 받아오고 DB에 저장
     */
    fun cacheCenterData(): Flow<RequestState>

    /**
     * DB에 저장되어있는 데이터가 있는지 확인
     */
    suspend fun isCachingCompleted(): Boolean
}

class CachingRepositoryImpl @Inject constructor(
    private val remoteApi: CentersApiInterface,
    private val database: CentersDao
) : CachingRepository {
    override fun cacheCenterData(): Flow<RequestState> = flow<RequestState> {
        for (page in 1..10) {
            val response = remoteApi.getCenters(pageIndex = page)

            database.insertList(response.data)
        }

        emit(RequestState.Success)
    }.catch { e ->
        e.printStackTrace()

        if (e is HttpException) {
            emit(RequestState.errorOfCode(e.code()))
        } else if (e is UnknownHostException) {
            emit(RequestState.NetworkError)
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun isCachingCompleted(): Boolean = (database.getDataCount() == 100)
}

package com.thk.vaccinationcenter.data.repository

import com.thk.vaccinationcenter.data.local.VaccinationCenterDatabase
import com.thk.vaccinationcenter.data.remote.CentersApiInterface
import com.thk.vaccinationcenter.data.utils.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import javax.inject.Inject

interface CachingRepository {
    fun cacheCenterData(): Flow<RequestState>
    fun isCachingCompleted(): Boolean
}

class CachingRepositoryImpl @Inject constructor(
    private val remoteApi: CentersApiInterface,
    private val database: VaccinationCenterDatabase
) : CachingRepository {
    override fun cacheCenterData(): Flow<RequestState> = flow<RequestState> {
        for (page in 1..10) {
            val response = remoteApi.getCenters(pageIndex = page)

            database.centersDao().insertList(response.data)
        }

        emit(RequestState.Success)
    }.catch { e ->
        e.printStackTrace()

        // TODO: 정리 필요
        if (e is HttpException) {

            /*
            * 400번대 코드 -> 클라이언트 에러
            * 500번대 코드 -> 서버 에러
            * */

            if (e.code().toString().startsWith("4")) {
                emit(RequestState.NetworkError)
            } else if (e.code().toString().startsWith("5")) {
                emit(RequestState.ServerError)
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun isCachingCompleted(): Boolean = (database.centersDao().getDataCount() == 100)
}
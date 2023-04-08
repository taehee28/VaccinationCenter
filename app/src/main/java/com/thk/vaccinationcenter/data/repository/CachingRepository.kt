
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
    fun cacheCenterData(): Flow<RequestState>
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

        /*delay(3000)*/

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
        } else if (e is UnknownHostException) {
            emit(RequestState.NetworkError)
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun isCachingCompleted(): Boolean = (database.getDataCount() == 100)
}
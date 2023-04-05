@file:OptIn(ExperimentalPagingApi::class)

package com.thk.vaccinationcenter.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.thk.vaccinationcenter.data.local.CentersDao
import com.thk.vaccinationcenter.data.local.VaccinationCenterDatabase
import com.thk.vaccinationcenter.data.remote.CentersApiInterface
import com.thk.vaccinationcenter.models.VaccinationCenter
import com.thk.vaccinationcenter.models.VaccinationCenterResponse
import com.thk.vaccinationcenter.utils.logd
import retrofit2.HttpException

private const val STARTING_PAGE = 1
private const val LAST_ID = 100

class CentersRemoteMediator(
    private val remoteApi: CentersApiInterface,
    private val database: VaccinationCenterDatabase
) : RemoteMediator<Int, VaccinationCenter>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, VaccinationCenter>,
    ): MediatorResult {
        return try {

            logd(">> loadType = ${loadType.name}")
            val nextPage: Int = when (loadType) {
                LoadType.REFRESH -> STARTING_PAGE
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem: VaccinationCenter = state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)

                    if (lastItem.id == LAST_ID) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    } else {
                        (lastItem.id / 10) + 1
                    }
                }
            }
            logd(">> nextPage = $nextPage")

            val response: VaccinationCenterResponse = remoteApi.getCenters(pageIndex = nextPage)
            logd(">> response(size=${response.data.size} = $response")

            database.withTransaction {
                database.centersDao().insertList(response.data)
            }

            MediatorResult.Success(
                endOfPaginationReached = (response.page == 10)
            )

        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}
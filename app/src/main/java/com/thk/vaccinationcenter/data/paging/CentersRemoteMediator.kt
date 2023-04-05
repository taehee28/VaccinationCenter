@file:OptIn(ExperimentalPagingApi::class)

package com.thk.vaccinationcenter.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.thk.vaccinationcenter.data.remote.CentersApiInterface
import com.thk.vaccinationcenter.models.VaccinationCenter
import com.thk.vaccinationcenter.models.VaccinationCenterResponse
import com.thk.vaccinationcenter.utils.logd
import retrofit2.HttpException

private const val STARTING_PAGE = 1

class CentersRemoteMediator(
    private val remoteApi: CentersApiInterface
) : RemoteMediator<Int, VaccinationCenterResponse>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, VaccinationCenterResponse>,
    ): MediatorResult {
        return try {

            logd(">> loadType = ${loadType.name}")
            val nextPage: Int = when (loadType) {
                LoadType.REFRESH -> STARTING_PAGE
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem: VaccinationCenterResponse = state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)

                    if (lastItem.page == 10) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    } else {
                        lastItem.page + 1
                    }
                }
            }
            logd(">> nextPage = $nextPage")

            val response: VaccinationCenterResponse = remoteApi.getCenters(pageIndex = nextPage)
            logd(">> response = $response")

            // TODO: DB에 저장

            MediatorResult.Success(
                endOfPaginationReached = (response.page == 10)
            )

        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}
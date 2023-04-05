@file:OptIn(ExperimentalPagingApi::class)

package com.thk.vaccinationcenter.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.thk.vaccinationcenter.data.local.CentersDao
import com.thk.vaccinationcenter.data.local.VaccinationCenterDatabase
import com.thk.vaccinationcenter.data.paging.CentersRemoteMediator
import com.thk.vaccinationcenter.data.remote.CentersApiInterface
import com.thk.vaccinationcenter.models.VaccinationCenter
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface CachingRepository {
    fun cacheCenterData(): Flow<PagingData<VaccinationCenter>>
    fun hasCenterData(): Boolean
}

class CachingRepositoryImpl @Inject constructor(
    private val remoteApi: CentersApiInterface,
    private val database: VaccinationCenterDatabase
) : CachingRepository {

    override fun cacheCenterData(): Flow<PagingData<VaccinationCenter>> {
        return Pager(
            config = PagingConfig(
                pageSize = 1
            ),
            remoteMediator = CentersRemoteMediator(
                remoteApi = remoteApi,
                database = database
            ),
            pagingSourceFactory = { database.centersDao().pagingSource() }
        ).flow
    }

    override fun hasCenterData(): Boolean = (database.centersDao().getDataCount() > 0)
}
package com.thk.vaccinationcenter.data.repository

import com.thk.vaccinationcenter.data.local.CentersDao
import com.thk.vaccinationcenter.data.remote.CentersApiInterface
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface CachingRepository {
    fun cacheCenterData(): Flow<Unit>
    fun hasCenterData(): Boolean
}

class CachingRepositoryImpl @Inject constructor(
    private val remoteApi: CentersApiInterface,
    private val database: CentersDao
) : CachingRepository {
    override fun cacheCenterData(): Flow<Unit> {
        TODO("Not yet implemented")
    }

    override fun hasCenterData(): Boolean = (database.getDataCount() > 0)
}
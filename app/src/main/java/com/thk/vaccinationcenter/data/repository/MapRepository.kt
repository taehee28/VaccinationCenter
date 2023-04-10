package com.thk.vaccinationcenter.data.repository

import com.thk.vaccinationcenter.data.local.CentersDao
import com.thk.vaccinationcenter.models.VaccinationCenter
import com.thk.vaccinationcenter.utils.logd
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface MapRepository {
    /**
     * DB에 저장되어있는 예방접종센터 데이터를 가져옴
     */
    fun getCenterList(): Flow<List<VaccinationCenter>>
}

class MapRepositoryImpl @Inject constructor(
    private val database: CentersDao
) : MapRepository {
    override fun getCenterList(): Flow<List<VaccinationCenter>> = database.getCenterList()
}
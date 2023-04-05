package com.thk.vaccinationcenter.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thk.vaccinationcenter.data.utils.DBInfo
import com.thk.vaccinationcenter.models.VaccinationCenter

@Dao
interface CentersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(centerList: List<VaccinationCenter>)

    @Query("SELECT * FROM ${DBInfo.TABLE_NAME}")
    fun pagingSource(): PagingSource<Int, VaccinationCenter>

    @Query("SELECT COUNT(*) FROM ${DBInfo.TABLE_NAME}")
    fun getDataCount(): Int
}
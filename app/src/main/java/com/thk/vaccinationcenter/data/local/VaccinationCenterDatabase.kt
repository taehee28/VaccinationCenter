package com.thk.vaccinationcenter.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.thk.vaccinationcenter.data.utils.DBInfo
import com.thk.vaccinationcenter.models.VaccinationCenter

@Database(
    version = DBInfo.DB_VERSION,
    entities = [VaccinationCenter::class]
)
abstract class VaccinationCenterDatabase : RoomDatabase() {
    abstract fun centersDao(): CentersDao
}
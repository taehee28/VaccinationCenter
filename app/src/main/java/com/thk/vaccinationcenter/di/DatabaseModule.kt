package com.thk.vaccinationcenter.di

import android.content.Context
import androidx.room.Room
import com.thk.vaccinationcenter.data.local.CentersDao
import com.thk.vaccinationcenter.data.local.VaccinationCenterDatabase
import com.thk.vaccinationcenter.data.utils.DBInfo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideVaccinationCenterDatabase(
        @ApplicationContext context: Context
    ): VaccinationCenterDatabase =
        Room.databaseBuilder(
            context = context,
            klass = VaccinationCenterDatabase::class.java,
            name = DBInfo.DB_NAME
        ).build()

    @Singleton
    @Provides
    fun provideCentersDao(database: VaccinationCenterDatabase): CentersDao =
        database.centersDao()
}
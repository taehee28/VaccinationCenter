package com.thk.vaccinationcenter.di

import com.thk.vaccinationcenter.data.repository.CachingRepository
import com.thk.vaccinationcenter.data.repository.CachingRepositoryImpl
import com.thk.vaccinationcenter.data.repository.MapRepository
import com.thk.vaccinationcenter.data.repository.MapRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindCachingRepository(cachingRepositoryImpl: CachingRepositoryImpl): CachingRepository

    @Binds
    abstract fun bindMapRepository(mapRepositoryImpl: MapRepositoryImpl): MapRepository
}
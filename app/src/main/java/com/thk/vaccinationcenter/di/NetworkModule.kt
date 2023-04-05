package com.thk.vaccinationcenter.di

import com.thk.vaccinationcenter.data.remote.CentersApiInterface
import com.thk.vaccinationcenter.data.utils.ApiInfo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideCentersApiInterface(retrofit: Retrofit): CentersApiInterface =
        retrofit.create(CentersApiInterface::class.java)

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(ApiInfo.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}
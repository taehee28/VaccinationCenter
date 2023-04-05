package com.thk.vaccinationcenter.data.remote

import com.thk.vaccinationcenter.BuildConfig
import com.thk.vaccinationcenter.models.VaccinationCenterResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CentersApiInterface {
    @GET("centers")
    suspend fun getCenters(
        @Query("serviceKey") apiKey: String = BuildConfig.API_KEY,
        @Query("page") pageIndex: Int = 1,
        @Query("perPage") perPage: Int = 10
    ): VaccinationCenterResponse
}
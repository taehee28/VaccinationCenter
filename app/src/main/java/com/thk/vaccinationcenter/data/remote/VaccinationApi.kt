package com.thk.vaccinationcenter.data.remote

import com.thk.vaccinationcenter.data.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal object VaccinationApi {
    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val _api = retrofit.create(VaccinationApiService::class.java)
    val api
        get() = _api
}
package com.thk.vaccinationcenter.data.remote

import com.thk.vaccinationcenter.data.utils.ApiInfo
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal object CentersApi {
    private val retrofit = Retrofit.Builder()
        .baseUrl(ApiInfo.API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val _api = retrofit.create(CentersApiInterface::class.java)
    val api
        get() = _api
}
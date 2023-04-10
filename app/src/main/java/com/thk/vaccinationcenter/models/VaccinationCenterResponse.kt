package com.thk.vaccinationcenter.models

import com.google.gson.annotations.SerializedName

data class VaccinationCenterResponse(
    @SerializedName("page")
    val page: Int = 0,
    @SerializedName("perPage")
    val perPage: Int = 0,
    @SerializedName("totalCount")
    val totalCount: Int = 0,
    @SerializedName("currentCount")
    val currentCount: Int = 0,
    @SerializedName("matchCount")
    val matchCount: Int = 0,
    @SerializedName("data")
    val data: List<VaccinationCenter>
)

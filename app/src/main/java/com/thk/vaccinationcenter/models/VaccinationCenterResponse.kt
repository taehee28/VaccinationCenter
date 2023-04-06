package com.thk.vaccinationcenter.models

data class VaccinationCenterResponse(
    val page: Int = 0,
    val perPage: Int = 0,
    val totalCount: Int = 0,
    val currentCount: Int = 0,
    val matchCount: Int = 0,
    val data: List<VaccinationCenter>
)

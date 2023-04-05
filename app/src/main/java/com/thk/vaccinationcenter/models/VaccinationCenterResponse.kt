package com.thk.vaccinationcenter.models

data class VaccinationCenterResponse(
    val page: Int,
    val perPage: Int,
    val totalCount: Int,
    val currentCount: Int,
    val matchCount: Int,
    val data: List<VaccinationCenter>
)

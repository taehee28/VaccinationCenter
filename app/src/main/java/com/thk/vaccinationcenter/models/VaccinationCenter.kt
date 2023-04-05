package com.thk.vaccinationcenter.models

data class VaccinationCenter(
    val id: Int,
    val centerName: String,
    val sido: String,
    val sigungu: String,
    val facilityName: String,   // 시설 이름
    val zipCode: String,        // 우편번호
    val address: String,        // 도로명 주소
    val lat: String,            // 위도
    val lng: String,            // 경도
    val createdAt: String,
    val updatedAt: String,
    val centerType: String,
    val org: String,
    val phoneNumber: String
)

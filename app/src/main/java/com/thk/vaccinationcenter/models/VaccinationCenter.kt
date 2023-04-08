package com.thk.vaccinationcenter.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.thk.vaccinationcenter.data.utils.DBInfo

@Entity(
    tableName = DBInfo.TABLE_NAME,
    indices = [
        Index(value = ["sido"])
    ]
)
data class VaccinationCenter(
    @PrimaryKey val id: Int,
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

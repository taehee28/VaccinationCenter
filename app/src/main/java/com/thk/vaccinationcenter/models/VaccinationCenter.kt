package com.thk.vaccinationcenter.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.thk.vaccinationcenter.data.utils.DBInfo

@Entity(tableName = DBInfo.TABLE_NAME)
data class VaccinationCenter(
    @SerializedName("id")
    @PrimaryKey
    val id: Int = 0,
    @SerializedName("centerName")
    val centerName: String = "",
    @SerializedName("sido")
    val sido: String = "",
    @SerializedName("sigungu")
    val sigungu: String = "",
    @SerializedName("facilityName")
    val facilityName: String = "",   // 시설 이름
    @SerializedName("zipCode")
    val zipCode: String = "",        // 우편번호
    @SerializedName("address")
    val address: String = "",        // 도로명 주소
    @SerializedName("lat")
    val lat: String = "",            // 위도
    @SerializedName("lng")
    val lng: String = "",            // 경도
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("updatedAt")
    val updatedAt: String = "",
    @SerializedName("centerType")
    val centerType: String = "",
    @SerializedName("org")
    val org: String = "",
    @SerializedName("phoneNumber")
    val phoneNumber: String = ""
)

package com.thk.vaccinationcenter.ui.map

import android.graphics.Color
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.MarkerIcons
import com.thk.vaccinationcenter.R
import com.thk.vaccinationcenter.models.VaccinationCenter

object CenterMarker {
    fun create(
        centerInfo: VaccinationCenter
    ): Marker = Marker().apply {
        position = LatLng(centerInfo.lat.toDouble(), centerInfo.lng.toDouble())
        captionText = centerInfo.centerName
        icon = when (CenterType.fromString(centerInfo.centerType)) {
            CenterType.CENTRAL -> MarkerIcons.PINK
            CenterType.LOCAL -> MarkerIcons.LIGHTBLUE
            else -> MarkerIcons.GREEN
        }

        tag = centerInfo
    }
}

enum class CenterType(val koType: String) {
    CENTRAL("중앙/권역"),
    LOCAL("지역");

    companion object {
        fun fromString(type: String): CenterType? = when (type) {
            CENTRAL.koType -> CENTRAL
            LOCAL.koType -> LOCAL
            else -> null
        }
    }
}
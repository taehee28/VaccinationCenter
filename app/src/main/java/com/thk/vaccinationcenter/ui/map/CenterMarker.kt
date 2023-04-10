package com.thk.vaccinationcenter.ui.map

import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MarkerIcons
import com.thk.vaccinationcenter.models.VaccinationCenter

/**
 * [VaccinationCenter] 타입의 데이터로 알맞은 [Marker] 인스턴스 만들어주는 object 클래스
 */
object CenterMarker {
    fun create(
        centerInfo: VaccinationCenter
    ): Marker = Marker().apply {
        position = LatLng(centerInfo.lat.toDouble(), centerInfo.lng.toDouble())

        captionText = centerInfo.centerName
        captionRequestedWidth = 200

        isHideCollidedCaptions = true
        isHideCollidedSymbols = true

        icon = when (CenterType.fromString(centerInfo.centerType)) {
            CenterType.CENTRAL -> MarkerIcons.PINK
            CenterType.LOCAL -> MarkerIcons.LIGHTBLUE
            else -> MarkerIcons.GREEN
        }

        tag = centerInfo
    }
}

/**
 * 예방접종센터 타입 enum
 */
enum class CenterType(val koType: String) {
    CENTRAL("중앙/권역"),
    LOCAL("지역");

    companion object {
        /**
         * 넘겨준 String에 해당하는 [CenterType] 반환
         */
        fun fromString(type: String): CenterType? = when (type) {
            CENTRAL.koType -> CENTRAL
            LOCAL.koType -> LOCAL
            else -> null
        }
    }
}
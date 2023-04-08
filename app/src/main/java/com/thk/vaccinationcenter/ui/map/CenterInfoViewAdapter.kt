package com.thk.vaccinationcenter.ui.map

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.naver.maps.map.overlay.InfoWindow
import com.thk.vaccinationcenter.R
import com.thk.vaccinationcenter.databinding.ViewCenterInfoBinding
import com.thk.vaccinationcenter.models.VaccinationCenter

/**
 * 마커 선택 시 표시되는 정보안내창 View의 Adapter
 */
class CenterInfoViewAdapter(context: Context) : InfoWindow.DefaultViewAdapter(context) {
    override fun getContentView(infoWindow: InfoWindow): View {
        val binding = ViewCenterInfoBinding.inflate(LayoutInflater.from(context))

        val centerInfo = infoWindow.marker?.tag as VaccinationCenter
        binding.apply {
            tvCenterName.text = centerInfo.centerName
            tvAddress.text = centerInfo.address
            tvFacilityName.text = centerInfo.facilityName
            tvPhoneNumber.text = centerInfo.phoneNumber.ifBlank { "-" }
            tvUpdatedAt.text = centerInfo.updatedAt
        }

        return binding.root
    }
}
package com.thk.vaccinationcenter.ui.map

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.NaverMapSdk.AuthFailedException
import com.naver.maps.map.OnMapReadyCallback
import com.thk.vaccinationcenter.R
import com.thk.vaccinationcenter.databinding.ActivityMapBinding
import com.thk.vaccinationcenter.utils.logd
import com.thk.vaccinationcenter.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 네이버 지도 예외 처리
        NaverMapSdk.getInstance(this).onAuthFailedListener =
            NaverMapSdk.OnAuthFailedListener { onAuthFailed(it) }

        // 지도 객체 요청
        (binding.map.getFragment<MapFragment>()).getMapAsync(this)
    }

    private fun onAuthFailed(exception: AuthFailedException) {
        exception.printStackTrace()

        val message = getString(R.string.toast_map_auth_failed).format(exception.errorCode)
        showToast(context = this, message = message)
    }

    override fun onMapReady(map: NaverMap) {
        map.locationOverlay.isVisible = true
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, MapActivity::class.java)
    }
}
package com.thk.vaccinationcenter.ui.map

import android.Manifest
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.naver.maps.map.*
import com.naver.maps.map.NaverMapSdk.AuthFailedException
import com.naver.maps.map.util.FusedLocationSource
import com.thk.vaccinationcenter.R
import com.thk.vaccinationcenter.databinding.ActivityMapBinding
import com.thk.vaccinationcenter.utils.logd
import com.thk.vaccinationcenter.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapBinding

    private lateinit var naverMap: NaverMap
    /*private val locationSource: FusedLocationSource by lazy {
        FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }*/

    private lateinit var locationSource: FusedLocationSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 네이버 지도 예외 처리
        NaverMapSdk.getInstance(this).onAuthFailedListener =
            NaverMapSdk.OnAuthFailedListener { onAuthFailed(it) }

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        // 지도 객체 요청
        (binding.map.getFragment<MapFragment>()).getMapAsync(this)

        checkLocationPermission()
    }

    private fun onAuthFailed(exception: AuthFailedException) {
        exception.printStackTrace()

        val message = getString(R.string.toast_map_auth_failed).format(exception.errorCode)
        showToast(context = this, message = message)
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map.apply {
            locationOverlay.isVisible = true
            this.locationSource = locationSource

            addOnLocationChangeListener { location ->
                logd(">> location = $location")
                /*showToast(context = this@MapActivity, message = "${location.latitude}, ${location.longitude}")*/
            }
        }
        naverMap.uiSettings.isLocationButtonEnabled = true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) {
                logd(">> 권한 거부됨")
                naverMap.locationTrackingMode = LocationTrackingMode.None
            } else {
                naverMap.locationTrackingMode = LocationTrackingMode.Follow
            }

            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun checkLocationPermission() {
        val isGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PermissionChecker.PERMISSION_GRANTED

        logd(">> isGranted = $isGranted")
        if (isGranted) return

        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            // 이전에 거부한 적이 있음
            AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(R.string.dialog_message_permission_information)
                .setPositiveButton(R.string.dialog_button_confirm) { _, _ ->
                    requestLocationPermission()
                }
        } else {
            // 처음 요청
            requestLocationPermission()
        }

    }

    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        fun getIntent(context: Context) = Intent(context, MapActivity::class.java)
    }
}
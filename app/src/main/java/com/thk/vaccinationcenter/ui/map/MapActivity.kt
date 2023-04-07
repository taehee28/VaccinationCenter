package com.thk.vaccinationcenter.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.NaverMapSdk.AuthFailedException
import com.thk.vaccinationcenter.R
import com.thk.vaccinationcenter.databinding.ActivityMapBinding
import com.thk.vaccinationcenter.utils.logd
import com.thk.vaccinationcenter.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapBinding

    private lateinit var naverMap: NaverMap

    private val isPermissionGranted: Boolean
        get() = (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) and
                (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)

    private val locationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_BALANCED_POWER_ACCURACY,
        1000
    ).build()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                onLocationChanged(location)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 네이버 지도 예외 처리
        NaverMapSdk.getInstance(this).onAuthFailedListener =
            NaverMapSdk.OnAuthFailedListener { onAuthFailed(it) }


        // 네이버 지도 객체 요청
        (binding.map.getFragment<MapFragment>()).getMapAsync { map ->
            naverMap = map.apply {
                locationOverlay.isVisible = true
            }
        }

        // TODO: 퍼미션을 확인하고 각종 초기화 진행하도록 변경

        // 마지막 위치가 있다면 표시
        locationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location ?: return@addOnSuccessListener
            onLocationChanged(location)
        }

        checkLocationPermission()
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        if (isPermissionGranted) {
            locationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    override fun onPause() {
        super.onPause()
        locationClient.removeLocationUpdates(locationCallback)
    }

    private fun onAuthFailed(exception: AuthFailedException) {
        exception.printStackTrace()

        val message = getString(R.string.toast_map_auth_failed).format(exception.errorCode)
        showToast(context = this, message = message)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        logd(">> onRequestPermissionsResult")
        // TODO: 거부 시 사용 불가하다는 안내 메시지 표시
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun checkLocationPermission() {

        if (isPermissionGranted) return

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

    /**
     * 현재 위치가 바뀔 때마다 현재 위치 아이콘의 위치와 카메라 이동을 설정
     */
    private fun onLocationChanged(location: Location) {
        naverMap.also {
            val coord = LatLng(location)

            it.locationOverlay.apply {
                position = coord
                bearing = location.bearing
            }

            it.moveCamera(CameraUpdate.scrollTo(coord))
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        fun getIntent(context: Context) = Intent(context, MapActivity::class.java)
    }
}
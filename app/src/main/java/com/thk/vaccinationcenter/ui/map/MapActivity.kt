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
            locationResult.locations.forEach { onLocationChanged(location = it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 네이버 지도 예외 처리
        NaverMapSdk.getInstance(this).onAuthFailedListener =
            NaverMapSdk.OnAuthFailedListener { onAuthFailed(it) }

        // 위치 정보 권한 확인하고 지도 설정
        if (isPermissionGranted) {
            initializeMap()
        } else {
            requestLocationPermission()
        }
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

    /**
     * 네이버 지도 예외 처리
     */
    private fun onAuthFailed(exception: AuthFailedException) {
        exception.printStackTrace()

        val message = getString(R.string.toast_map_auth_failed).format(exception.errorCode)
        showToast(context = this, message = message)
    }

    /**
     * 지도 사용하기 위해 초기화 수행
     */
    @SuppressLint("MissingPermission")
    private fun initializeMap() {
        // 네이버 지도 객체 요청
        (binding.map.getFragment<MapFragment>()).getMapAsync { map ->
            naverMap = map.apply {
                locationOverlay.isVisible = true
            }

            // 마지막 위치가 있다면 표시
            // 네이버 맵 객체가 초기화 된 후에 리스너 추가
            locationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location ?: return@addOnSuccessListener
                onLocationChanged(location = location, moveCamera = true)
            }
        }
    }

    /**
     * 현재 위치가 바뀔 때마다 현재 위치 아이콘의 위치와 카메라 이동을 설정
     */
    private fun onLocationChanged(location: Location, moveCamera: Boolean = false) {
        naverMap.also {
            val coord = LatLng(location)

            it.locationOverlay.apply {
                position = coord
                bearing = location.bearing
            }

            if (moveCamera) {
                it.moveCamera(CameraUpdate.scrollTo(coord))
            }
        }
    }

    /**
     * 위치 정보 권한 요청
     */
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
     * 권한 요청 결과 callback
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                val result = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
                if (result) {
                    initializeMap()
                } else {
                    showToast(this, R.string.toast_map_permission_denied)
                }
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000

        fun getIntent(context: Context) = Intent(context, MapActivity::class.java)
    }
}
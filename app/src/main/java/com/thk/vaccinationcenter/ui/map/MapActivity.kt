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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.NaverMapSdk.AuthFailedException
import com.naver.maps.map.overlay.Marker
import com.thk.vaccinationcenter.R
import com.thk.vaccinationcenter.databinding.ActivityMapBinding
import com.thk.vaccinationcenter.models.VaccinationCenter
import com.thk.vaccinationcenter.utils.logd
import com.thk.vaccinationcenter.utils.showToast
import com.thk.vaccinationcenter.utils.toLatLng
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapBinding

    private lateinit var naverMap: NaverMap

    private val isPermissionGranted: Boolean
        get() = (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) and
                (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)

    @Inject lateinit var locationClient: FusedLocationProviderClient

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_BALANCED_POWER_ACCURACY,
        1000
    ).build()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.locations.forEach { onLocationChanged(location = it) }
        }
    }

    private var currentLocation: Location? = null

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
                // 사용자 위치 오버레이 표시 설정
                locationOverlay.isVisible = true

                // 최소 줌 제한
                minZoom = 5.0

                // 한반도 인근으로 카메라 이동 제한
                extent = LatLngBounds(
                    LatLng(31.43, 122.37),
                    LatLng(44.35, 132.0)
                )
            }

            // 마지막 위치가 있다면 표시
            // 네이버 맵 객체가 초기화 된 후에 리스너 추가
            locationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location ?: return@addOnSuccessListener
                onLocationChanged(location = location, isCameraMoving = true)

                // FIXME: 마커 표시 샘플
                CenterMarker.create(
                    VaccinationCenter(
                        centerName = "예방접종센터",
                        centerType = "지역",
                        lat = location.latitude.toString(),
                        lng = location.longitude.toString()
                    )
                ).apply {
                    setOnClickListener { overlay ->
                        val marker = overlay as Marker
                        moveCamera(marker.position, CameraAnimation.Easing)

                        true
                    }

                    this.map = naverMap

                }
            }

            // 현재 위치 버튼
            binding.btnMyLocation.setOnClickListener {
                currentLocation?.also { moveCamera(it.toLatLng()) }
            }
        }
    }

    /**
     * 현재 위치가 바뀔 때마다 현재 위치 아이콘의 위치와 카메라 이동을 설정
     */
    private fun onLocationChanged(location: Location, isCameraMoving: Boolean = false) {
        currentLocation = location

        naverMap.locationOverlay.apply {
            position = location.toLatLng()
            bearing = location.bearing
        }

        if (isCameraMoving) {
            moveCamera(location.toLatLng())
        }
    }

    /**
     * 카메라 이동
     */
    private fun moveCamera(latLng: LatLng, animation: CameraAnimation = CameraAnimation.None) {
        naverMap.moveCamera(
            CameraUpdate
                .scrollTo(latLng)
                .animate(animation)
        )
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
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
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.NaverMapSdk.AuthFailedException
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.thk.vaccinationcenter.R
import com.thk.vaccinationcenter.databinding.ActivityMapBinding
import com.thk.vaccinationcenter.models.VaccinationCenter
import com.thk.vaccinationcenter.utils.showToast
import com.thk.vaccinationcenter.utils.toLatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapBinding
    private val viewModel: MapViewModel by viewModels()

    private var naverMap: NaverMap? = null
    @Inject lateinit var centerInfoView: InfoWindow
    @Inject lateinit var locationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null

    // 위치 변경 콜백 등록할 때 같이 넘겨주는 Request
    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_BALANCED_POWER_ACCURACY,
        1000
    ).build()

    // 위치 변경 콜백
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.locations.forEach { onLocationChanged(location = it) }
        }
    }

    // 퍼미션 확인용 프로퍼티
    private val isPermissionGranted: Boolean
        get() = (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) and
                (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 네이버 지도 예외 처리
        NaverMapSdk.getInstance(this).onAuthFailedListener =
            NaverMapSdk.OnAuthFailedListener { onAuthFailed(it) }

        // 현재 위치 버튼
        binding.btnMyLocation.setOnClickListener {
            currentLocation?.also { moveCamera(it.toLatLng()) }
        }

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
        // 위치 변경 콜백 등록
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
        // 위치 변경 콜백 해제
        locationClient.removeLocationUpdates(locationCallback)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 마지막 카메라 위치 유지하도록 설정
        viewModel.keepLastCameraPosition = true
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

                setOnMapClickListener { _, _ -> centerInfoView.close() }
            }

            // 마지막 위치가 있다면 표시
            // 네이버 맵 객체가 초기화 된 후에 리스너 추가
            locationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location ?: return@addOnSuccessListener
                onLocationChanged(
                    location = location,
                    isCameraMoving = viewModel.keepLastCameraPosition.not()
                )
            }

            addMarkerToMap()
        }
    }

    /**
     * DB에 저장된 데이터를 받아와 지도에 마커 표시
     */
    private fun addMarkerToMap() = lifecycleScope.launch {
        viewModel.centerList.collectLatest { list: List<VaccinationCenter> ->
            list.onEach { center ->
                CenterMarker.create(centerInfo = center).apply {
                    setOnClickListener { overlay ->
                        val marker = overlay as Marker
                        moveCamera(marker.position, CameraAnimation.Easing)

                        if (marker.infoWindow == null) {
                            centerInfoView.open(marker)
                        } else {
                            centerInfoView.close()
                        }

                        true
                    }

                    map = naverMap
                }
            }
        }
    }

    /**
     * 현재 위치가 바뀔 때마다 현재 위치 아이콘의 위치와 카메라 이동을 설정
     */
    private fun onLocationChanged(location: Location, isCameraMoving: Boolean = false) {
        currentLocation = location

        naverMap?.locationOverlay?.apply {
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
        naverMap?.moveCamera(
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
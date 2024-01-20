package kr.easw.estrader.android.util

import android.Manifest
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import kr.easw.estrader.android.util.MapUtil.moveCameraToPosition
import kr.easw.estrader.android.util.MapUtil.setLocationOverlayPosition

object LocationUtil {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private const val INTERVAL = 1000L
    private const val MINIMAL_DISTANCE = 0f
    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    // 통합 위치 제공자 초기화
    fun initLocationClient(fragment: Fragment) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(fragment.requireContext())
        locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, INTERVAL).apply {
                setMinUpdateDistanceMeters(MINIMAL_DISTANCE)
                setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                setWaitForAccurateLocation(true)
            }.build()
        locationCallback = object: LocationCallback() {}
        Log.d("X3 | initLocationClient()", "통합 위치 제공자 초기화")
    }

    // 마지막 위치 '한 번만' 요청
    // 관련 링크 : https://duzi077.tistory.com/263
    // 관련 링크 : https://manorgass.tistory.com/82
    fun loadCurrentLocation(fragment: Fragment, naverMap: NaverMap) {
        if (ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // 위치에 대한 데이터 를 갱신 후 현재 위치 정보 받기 가능
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                Log.d("X3 | loadCurrentLocation()", "마지막 위치 '한 번만' 요청")
                location?.let {
                    Log.d("X3 | loadCurrentLocation()", "${it.latitude} | ${it.longitude}")
                } ?: run {
                    Log.d("X3 | loadCurrentLocation()", "위치 정보 가져오기 실패")
                }
                setCameraAndUserLocation(naverMap, LatLng(36.6300312, 127.455085))
            }
        } else {
            fusedLocationPermissionLauncher(fragment, naverMap).launch(PERMISSIONS)
        }
    }

    // 위치 권한 요청
    // 관련 링크 : https://velog.io/@dear_jjwim/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-registerForActivityResult
    private fun fusedLocationPermissionLauncher(fragment: Fragment, naverMap: NaverMap) =
        fragment.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            // 위치 권한을 받지 못하면, default 위치 설정
            for(entry in it.entries) {
                if(!entry.value) {
                    Log.d("X3 | fusedLocationPermissionLauncher()", "권한으로 인한 위치 정보 가져오기 실패")
                    showToast(fragment,"위치 정보 접근 권한이 거부되었습니다. 기본 위치로 설정합니다.")
                    setCameraAndUserLocation(naverMap, LatLng(36.6300312, 127.455085))
                    return@registerForActivityResult
                }
            }
        }

    private fun setCameraAndUserLocation(map: NaverMap, latLng: LatLng) {
        moveCameraToPosition(map, latLng)
        setLocationOverlayPosition(map, latLng)
    }

    private fun showToast(fragment: Fragment, message: String) {
        Toast.makeText(fragment.requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
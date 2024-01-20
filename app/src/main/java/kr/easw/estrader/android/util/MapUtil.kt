package kr.easw.estrader.android.util

import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMap.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import kr.easw.estrader.android.model.dto.MainItem
import java.util.*

object MapUtil {
    private lateinit var geocoder: Geocoder
    private var flag = false
    private val markers: MutableList<Marker> = mutableListOf()

    // 대상 지점을 변경 하는 CameraUpdate 객체를 만들고 NaverMap.moveCamera()를 호출해 카메라 움직일 수 있는 예제
    // Docs 링크 : https://navermaps.github.io/android-map-sdk/guide-ko/3-2.html
    fun moveCameraToPosition(map: NaverMap, latLng: LatLng) {
        val cameraUpdate = CameraUpdate.scrollTo(latLng)
        map.moveCamera(cameraUpdate)
    }

    // Animation 효과가 있는 카메라 이동
    fun moveCameraWithAnimation(context: Context, map: NaverMap, latLng: LatLng) {
        val cameraUpdate = CameraUpdate.scrollTo(latLng)
            .animate(CameraAnimation.Easing)
            .finishCallback {
                Toast.makeText(context, "카메라 이동 완료", Toast.LENGTH_SHORT).show()
            }
            .cancelCallback {
                Toast.makeText(context, "카메라 이동 취소", Toast.LENGTH_SHORT).show()
            }
        map.moveCamera(cameraUpdate)
    }

    // 카메라 이동 콜백
    fun setupCameraChangeListener(map: NaverMap, action: (Int, Boolean) -> Unit) {
        map.addOnCameraChangeListener { reason, animated ->
            action.invoke(reason, animated)
        }
    }

    // 카메라 이동 후 정지 콜백
    fun setupCameraIdleListener(map: NaverMap, action: () -> Unit) {
        map.addOnCameraIdleListener  {
            action.invoke()
        }
    }

    // 네이버 지도 UI 설정
    fun configureMapUiSettings(
        map: NaverMap,
        Type: MapType,
        isBuildingEnabled: Boolean,
        isTrafficEnabled: Boolean,
        isTransitEnabled: Boolean,
        enableTouchMapScrolling: Boolean,
        enableTouchMapZooming: Boolean
    ) {
        map.apply {
            mapType = Type // 하천, 녹지, 도로, 심벌 등 다양한 정보를 노출하는 일반 지도
            minZoom = 5.0 // 카메라 최소 줌 레벨 제한
            maxZoom = 18.0 // 카메라 최대 줌 레벨 제한

            // UI 설정 설정
            // 네이버 지도 SDK 사용 앱은 반드시 네이버 로고가 앱의 UI 요소에 가리지 않도록 조정 필수
            uiSettings.apply {
                isCompassEnabled = true // 나침반
                isScaleBarEnabled = true // 축척 바
                isZoomControlEnabled = true // 줌 버튼
                isLocationButtonEnabled = false // 현 위치 버튼
                isLogoClickEnabled = true // 네이버 로고 클릭 활성화 여부

                isScrollGesturesEnabled = enableTouchMapScrolling // 스크롤 제스처
                isZoomGesturesEnabled = enableTouchMapZooming // 줌 제스처
            }

            // 레이어 그룹은 지도 유형과 달리 동시에 두 개 이상을 활성화 가능
            setLayerGroupEnabled(LAYER_GROUP_BUILDING, isBuildingEnabled) // 건물 그룹
            setLayerGroupEnabled(LAYER_GROUP_TRAFFIC, isTrafficEnabled) // 실시간 교통 정보 그룹
            setLayerGroupEnabled(LAYER_GROUP_TRANSIT, isTransitEnabled) // 대중 교통 그룹
        }
    }

    // 위치 오버레이는 사용자 위치를 나타내는 데 특화된 오버레이로, 지도 상에 단 하나만 존재
    fun setLocationOverlayPosition(map: NaverMap, latLng: LatLng) {
        val locationOverlay = map.locationOverlay
        locationOverlay.apply {
            iconWidth = LocationOverlay.SIZE_AUTO
            iconHeight = LocationOverlay.SIZE_AUTO
            circleRadius = 100
            isVisible = true
            position = latLng
        }
    }

    // 마커 추가
    fun addMarker(
        context: Context,
        lat: Double,
        lng: Double,
        color: OverlayImage,
        naverMap: NaverMap,
        item: MainItem
    ) {
        // isHideCollidedMarkers 속성을 true 지정 시, 마커가 다른 마커와 겹칠 경우 겹치는 마커 숨김
        // 위치 overlay 를 제외한 모든 overlay 는 map 속성을 지정해 overlay 를 지도에 추가 가능( marker.map = naverMap )
        // Docs 링크 : https://navermaps.github.io/android-map-sdk/guide-ko/5-2.html
        val marker = Marker().apply {
            icon = color
            captionMinZoom = 12.0
            captionMaxZoom = 16.0
            width = Marker.SIZE_AUTO
            height = Marker.SIZE_AUTO
            position = LatLng(lat, lng)
            isFlat = true // 지도를 기울이거나 회전하더라도 모양 유지
            isHideCollidedMarkers = true //  마커와 겹치는 다른 마커를 자동 숨김
            map = naverMap
        }

        // 정보 창 객체를 생성과 어댑터 지정 후 표시할 텍스트 작성
        val infoWindow = InfoWindow().apply {
            adapter = object : InfoWindow.DefaultTextAdapter(context) {
                override fun getText(infoWindow: InfoWindow): CharSequence {
                    return item.location
                }
            }
        }

        // overlay 클릭 이벤트 등록
        // overlay 클릭 이벤트 발생 후, 클릭 이벤트 를 지도로 전파 안하기 때문에 false 반환
        // Docs 링크 : https://navermaps.github.io/android-map-sdk/guide-ko/4-1.html
        marker.setOnClickListener {
            flag = !flag
            if (flag) {
                // 마커로 정보 창을 여는 예제
                // Docs 링크 : https://navermaps.github.io/android-map-sdk/guide-ko/5-1.html
                infoWindow.open(marker)
            } else {
                infoWindow.close()
            }
            true
        }

        markers.add(marker)
    }

    fun getCurrentAddress(
        context: Context,
        latitude: Double,
        longitude: Double
    ): String {
        geocoder = Geocoder(context, Locale.KOREAN)
        val addresses = geocoder.getFromLocation(
            latitude,
            longitude,
            1
        )

        if(addresses.isNullOrEmpty()) {
            return ""
        }

        return addresses[0].subLocality.toString()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun getCurrentAddressForTiramisu(
        context: Context,
        latitude: Double,
        longitude: Double
    ): String {
        var address = ""

        geocoder = Geocoder(context, Locale.KOREAN)
        geocoder.getFromLocation(
            latitude,
            longitude,
            1
        ) {
            address = it[0].subLocality.toString()
        }

        return address
    }
}

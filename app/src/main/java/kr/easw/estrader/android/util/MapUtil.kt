package kr.easw.estrader.android.util

import android.content.Context
import android.location.Geocoder
import android.util.Log
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.easw.estrader.android.fragment.user.MapFragment
import kr.easw.estrader.android.model.dto.ItemDto
import kr.easw.estrader.android.model.dto.MainItem
import java.io.IOException

object MapUtil {
    private var flag = false
    fun moveCameraToPosition(map: NaverMap, latLng: LatLng) {
        map.moveCamera(CameraUpdate.scrollTo(latLng))
    }

    fun printLog(tag: String, massage: String) {
        Log.d(tag, massage)
    }

    fun configureMapUiSettings(map: NaverMap, locationButtonEnabled: Boolean) {
        map.uiSettings.isLocationButtonEnabled = locationButtonEnabled
    }

    fun setLocationSource(map: NaverMap, locationSource: FusedLocationSource) {
        map.locationSource = locationSource
    }

    fun handleCameraChange(
        map: NaverMap,
        lastKnownLatLng: LatLng?,
        onCameraMoved: () -> Unit,
        updateLastKnownLatLng: (LatLng) -> Unit,
        shouldUpdateCameraMovement: (Boolean) -> Unit
    ) {

        val currentLatLng = map.cameraPosition.target
        if (lastKnownLatLng != null) {
            val distance = lastKnownLatLng!!.distanceTo(currentLatLng).toInt()
            if (distance >= 1000) { // 1000 미터 이상 움직였을 때
                onCameraMoved() // 콜백으로 처리한 이벤트 실행
                updateLastKnownLatLng(currentLatLng) // 마지막 알려진 위치 업데이트 콜백
                shouldUpdateCameraMovement(false) // 다시 false로 설정하는 콜백
            }
        } else {
            updateLastKnownLatLng(currentLatLng)
        }
    }

    fun addmarkers(lat: Double, lng: Double, naverMap: NaverMap, item: MainItem, context: Context) {
        val markers = mutableListOf<Marker>()
        val marker = Marker().apply {
            position = LatLng(lat, lng)
            map = naverMap
        }
        Log.d("ddd", "마커찌금")
        markers.add(marker)

        val infoWindow = InfoWindow().apply {
            adapter = object : InfoWindow.DefaultTextAdapter(context) {
                override fun getText(infoWindow: InfoWindow): CharSequence {
                    return item.location
                }
            }
        }
        marker.setOnClickListener {
            flag = !flag
            if (flag) {
                infoWindow.open(marker)
            } else {
                infoWindow.close()
            }
            true
        }
    }

    fun getFromLocationAsync(
        geocoder: Geocoder,
        latitude: Double,
        longitude: Double,
        listener: MapFragment.GeocodeListener
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                withContext(Dispatchers.Main) {
                    listener.onGeocodeReceived(addresses)
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    listener.onError(e)
                }
            }
        }
    }
    fun handleApiResults(context: Context, itemDtoList: List<ItemDto>, naverMap: NaverMap) {
        // 데이터 리스트를 이용하여 새 마커 추가
        for (itemDto in itemDtoList) {
            val newItem = MainItem(
                itemDto.photo,
                itemDto.court,
                itemDto.caseNumber,
                itemDto.location,
                itemDto.minimumBidPrice,
                itemDto.biddingPeriod,
                itemDto.xcoordinate,
                itemDto.ycoordinate
            )

            val lat = itemDto.ycoordinate.toDoubleOrNull() ?: continue
            val lng = itemDto.xcoordinate.toDoubleOrNull() ?: continue

            addmarkers(lat, lng, naverMap, newItem, context)
        }
    }

}

package kr.easw.estrader.android.fragment.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import kr.easw.estrader.android.R
import kr.easw.estrader.android.activity.user.MainActivity
import kr.easw.estrader.android.databinding.FragmentSearchRegionBinding
import kr.easw.estrader.android.fragment.viewBinding
import kr.easw.estrader.android.util.LocationUtil
import kr.easw.estrader.android.util.MapUtil

class SearchregionFragment : Fragment(), OnMapReadyCallback {
    private lateinit var naverMap: NaverMap
    private val mapBinding by viewBinding(FragmentSearchRegionBinding::bind)
    private lateinit var locationSource: FusedLocationSource
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastKnownLatLng: LatLng? = null
    private var cameraMoved = false

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_search_region, container, false)
        setupMapView(rootView)
        return rootView
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFields()

        val mainActivity = activity as? MainActivity
        mainActivity?.let {

        }

    }
    private fun initFields(){

    }
    override fun onMapReady(map: NaverMap) {
        Log.d("X3 | onMapReady()", "onMapReady 콜백 메서드 호출")
        naverMap = map

        MapUtil.configureMapUiSettings(
            naverMap,
            NaverMap.MapType.Basic,
            isBuildingEnabled = true,
            isTrafficEnabled = true,
            isTransitEnabled = true,
            enableTouchMapScrolling = true,
            enableTouchMapZooming = true
        )

        LocationUtil.initLocationClient(this)
        LocationUtil.loadCurrentLocation(this, naverMap)
    }

    private fun setupMapView(rootView: View) {
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.mapView) as com.naver.maps.map.MapFragment?
            ?: com.naver.maps.map.MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.mapView, it).commit()
            }

        mapFragment.getMapAsync(this)
    }


}
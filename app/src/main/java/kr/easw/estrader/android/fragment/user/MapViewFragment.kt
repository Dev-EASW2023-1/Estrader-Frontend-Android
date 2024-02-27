package kr.easw.estrader.android.fragment.user

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.MarkerIcons
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.easw.estrader.android.R
import kr.easw.estrader.android.abstracts.BasePagingAdapter
import kr.easw.estrader.android.abstracts.PaginationScrollListener
import kr.easw.estrader.android.databinding.ElementItemBinding
import kr.easw.estrader.android.databinding.FragmentBottomSheetRecyclerViewBinding
import kr.easw.estrader.android.databinding.FragmentMapBinding
import kr.easw.estrader.android.definitions.ApiDefinition
import kr.easw.estrader.android.definitions.PREFERENCE_TOKEN
import kr.easw.estrader.android.fragment.viewBinding
import kr.easw.estrader.android.model.dto.ItemPageRequestDTO
import kr.easw.estrader.android.model.dto.MainItem
import kr.easw.estrader.android.util.LocationUtil.initLocationClient
import kr.easw.estrader.android.util.LocationUtil.loadCurrentLocation
import kr.easw.estrader.android.util.MapUtil
import kr.easw.estrader.android.util.MapUtil.addMarker
import kr.easw.estrader.android.util.MapUtil.configureMapUiSettings
import kr.easw.estrader.android.util.MapUtil.setupCameraIdleListener
import kr.easw.estrader.android.util.PreferenceUtil
import kr.easw.estrader.android.util.ViewUtil
import kr.easw.estrader.android.util.ViewUtil.setupBottomSheetForMapView

/**
 * 네이버 지도 Fragment
 *
 * MapFragment을 자식 프래그먼트로 설정 후,
 * naverMap 객체가 준비되면, onMapReady() 콜백 메서드에서 지도 UI를 설정할 수 있다.
 * 또한, 지도 UI에 있는 검색 버튼을 누르면 bottomSheetLayout 안에 recyclerView와 Paging을 구현하였다.
 *
 */

// 지도 API 성능 비교:  https://xetown.com/questions/1532680
class MapViewFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {
    private val dataList: MutableList<MainItem> = mutableListOf()

    private lateinit var bottomSheetBinding: FragmentBottomSheetRecyclerViewBinding
    private lateinit var bottomSheetLayout: ConstraintLayout
    private lateinit var btnInitialize: AppCompatButton
    private lateinit var reloadButton: Button
    private lateinit var mapView: MapView
    private var naverMap: NaverMap? = null
    private var mapFragment: MapFragment? = null
    private lateinit var pagingAdapter: BasePagingAdapter<MainItem, ElementItemBinding>

    private val mapBinding by viewBinding(FragmentMapBinding::bind)

    private var currentPage: Int = 0
    private val totalPage: Int = 10
    private var size: Int = 5

    private var isLastPage: Boolean = false
    private var isPaging: Boolean = false

    companion object {
        private const val PAGE_START = 0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        initializeUIComponents()
        initRecycler()
    }

    // childFragmentManager 를 사용해 MapFragment id를 이용해 찾은 후에 FragmentTransaction 로 지도를 화면에 표시
    // MapFragment 를 다른 fragment 내에 배치할 경우 supportFragmentManager 대신 childFragmentManager 사용
    override fun onMapReady(map: NaverMap) {
        Log.d("X3 | onMapReady()", "onMapReady 콜백 메서드 호출")
        naverMap = map

        // map UI 설정
        configureMapUiSettings(
            naverMap!!,
            NaverMap.MapType.Basic,
            isBuildingEnabled = true,
            isTrafficEnabled = true,
            isTransitEnabled = true,
            enableTouchMapScrolling = true,
            enableTouchMapZooming = true
        )

        // 지도 안에서 사용할 검색 버튼 리스너 초기화
        initSearchButtonListener()

        // 카메라 움직임 멈추면 검색 버튼 visible
        setupCameraIdleListener(naverMap!!, action = { reloadButton.visibility = View.VISIBLE })

        // 통합 위치 제공자 초기화 및 위치 요청
        initLocationClient(this)
        loadCurrentLocation(this, naverMap!!)
    }

    private fun initializeUIComponents() {
        bottomSheetLayout = mapBinding.bottomSheet.root
        val toggleButton = mapBinding.btnInitialize
        // BottomSheetLayout 에 View Binding 적용
        // fragment_map.xml 안에 include 한 layout 도 따로 view Binding 적용 해야 한다.
        // 참고 : https://stackoverflow.com/questions/58730127/view-binding-how-do-i-get-a-binding-for-included-layouts
        bottomSheetBinding = FragmentBottomSheetRecyclerViewBinding.bind(bottomSheetLayout)
        setupBottomSheetForMapView(this, bottomSheetLayout)
    }

    private fun initSearchButtonListener() {
        reloadButton = mapBinding.reloadButton
        reloadButton.setOnClickListener {
            bottomSheetBinding.bottomSheetRecyclerView.visibility = View.VISIBLE
            reloadButton.visibility = View.GONE

            clearDataList()

            // 화면의 중심 좌표
            val center = naverMap!!.cameraPosition.target
            Log.d("X3 | showReloadButton()", "center 값 확인 $center")

            getDetailedAddress(center)
        }
    }

    // RecyclerView 페이징 적용
    private fun initRecycler() {
        pagingAdapter = ViewUtil.setupAdapterForMapView(this, dataList)
        bottomSheetBinding.mainlistRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            itemAnimator = DefaultItemAnimator()
            adapter = pagingAdapter
            addOnScrollListener(object :
                PaginationScrollListener(layoutManager as LinearLayoutManager) {
                override var pageSize: Int = size

                override fun loadMoreItems() {
                    isPaging = true
                    currentPage++
                    prepareListItem()
                    Log.d("X3 | BasePagingAdapter()", "loadMoreItems 이벤트 확인")
                }

                override fun isLastPage(): Boolean {
                    return isLastPage
                }

                override fun isPaging(): Boolean {
                    return isPaging
                }
            })
        }
    }

    private fun prepareListItem() {
        val dialog = Dialog(requireContext()).apply {
            setContentView(ProgressBar(requireContext()))
            show()
        }

        ApiDefinition.GET_ITEM_LIST.setRequestParams(
            ItemPageRequestDTO("상당구", currentPage, size)
        ).setListener { response ->
            response.itemDto.takeIf { it.isNotEmpty() }?.let {

                // 맨 처음 데이터 준비할 때, 남아 있는 로딩 뷰를 없앤다.
                if (currentPage != PAGE_START) pagingAdapter.removeLoadingView()

                it.forEach { item ->
                    val newItem = MainItem(
                        item.photo,
                        item.court,
                        item.caseNumber,
                        item.location,
                        item.minimumBidPrice,
                        item.biddingPeriod,
                        item.xcoordinate,
                        item.ycoordinate,
                        item.district
                    )

                    dataList.add(
                        newItem
                    )

                    // 페이징 처리할 때마다 새 마커 추가
                    // 위도, 경도 값이 null 일 경우 마커 추가 이벤트 무시
                    val lat = item.ycoordinate.toDoubleOrNull() ?: return@forEach
                    val lng = item.xcoordinate.toDoubleOrNull() ?: return@forEach
                    addMarker(requireContext(), lat, lng, MarkerIcons.GREEN, naverMap!!, newItem)
                }

                // 최대 상한인 totalPage 와 currentPage 비교 후 로딩 뷰 추가
                // 마지막 페이지 도달 시, isLastPage 를 true 로 설정
                if (currentPage < totalPage) {
                    pagingAdapter.addLoadingView(MainItem())
                } else {
                    isLastPage = true
                }

                // 페이징 작업 완료 시, isPaging 를 false 로 설정
                isPaging = false

            } ?: run {
                showToast("마지막 페이지 입니다.")
                pagingAdapter.removeLoadingView()
            }

            dialog.dismiss()
        }.setRequestHeaders(
                mutableMapOf(
                    "Authorization" to "Bearer " + PreferenceUtil(requireContext()).init().start()
                        .getString(PREFERENCE_TOKEN)!!
                )
            ).build(requireContext())
    }

    // Geocoder getFromLocation deprecated 로 인한 버전 핸들링 추가
    // 위도, 경도를 이용해 Geocoder 로 국내 주소 구하기
    // 참고 링크 : https://www.inflearn.com/questions/911936/geocoder-getfromlocation-%EC%97%90%EB%9F%AC
    private fun getDetailedAddress(center: LatLng) {
        CoroutineScope(Main).launch {
            runCatching {
                val fetchDistrict = withContext(IO) {
                    if (Build.VERSION.SDK_INT < 33) {
                        MapUtil.getCurrentAddress(
                            requireContext(), center.latitude, center.longitude
                        )
                    } else {
                        MapUtil.getCurrentAddressForTiramisu(
                            requireContext(), center.latitude, center.longitude
                        )
                    }
                }
                Log.d("x3 | getDetailedAddress()", "지역 이름 출력 $fetchDistrict")
                prepareListItem()
            }.onFailure { e ->
                println("CATCHING: ${e.message}")
                return@launch
            }
        }
    }

    // 검색 버튼을 누르면, recyclerView 안의 데이터 초기화
    private fun clearDataList() {
        currentPage = PAGE_START
        isLastPage = false
        pagingAdapter.clear()
    }
    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }
    override fun onDestroyView() {
        mapView.onDestroy()
        mapFragment = null
        naverMap = null
        super.onDestroyView()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}

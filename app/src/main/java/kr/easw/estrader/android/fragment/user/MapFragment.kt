package kr.easw.estrader.android.fragment.user

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import kr.easw.estrader.android.R
import kr.easw.estrader.android.databinding.ElementItemBinding
import kr.easw.estrader.android.databinding.FragmentBottomSheetBinding
import kr.easw.estrader.android.databinding.FragmentMainlistBinding
import kr.easw.estrader.android.databinding.FragmentMapBinding
import kr.easw.estrader.android.definitions.ApiDefinition
import kr.easw.estrader.android.definitions.Client_ID
import kr.easw.estrader.android.definitions.Client_Secret
import kr.easw.estrader.android.definitions.PREFERENCE_TOKEN
import kr.easw.estrader.android.fragment.BaseFragment
import kr.easw.estrader.android.fragment.viewBinding
import kr.easw.estrader.android.model.data.MainHolder
import kr.easw.estrader.android.model.dto.DistrictRequest
import kr.easw.estrader.android.model.dto.ItemDto
import kr.easw.estrader.android.model.dto.MainItem
import kr.easw.estrader.android.util.MapUtil
import kr.easw.estrader.android.util.PreferenceUtil
import kr.easw.estrader.android.util.SharedViewModel
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import java.lang.ref.WeakReference
import java.util.Locale


//https://xetown.com/questions/1532680
//지도당 속도
class MapFragment : BaseFragment<FragmentMainlistBinding>(FragmentMainlistBinding::inflate),
    OnMapReadyCallback {

    private lateinit var naverMap: NaverMap
    private lateinit var currentLocationButton: com.naver.maps.map.widget.LocationButtonView
    private lateinit var locationSource: FusedLocationSource
    private var lastKnownLatLng: LatLng? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: SharedViewModel by activityViewModels()
    private var cameraMoved = false
    private var recyclerBinding: ElementItemBinding? = null
    private var itemClickListener: WeakReference<OnItemClickListener>? = null
    private val markers = mutableListOf<Marker>()
    private var moredata = true
    private var currentPage = 0
    private var size = 5
    private var isLoading = false
    private val dataList: MutableList<MainItem> = mutableListOf()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private val mapBinding by viewBinding(FragmentMapBinding::bind)
    private var bottomSheetBinding: FragmentBottomSheetBinding? = null
    private lateinit var bottomSheetLayout: ConstraintLayout
    private lateinit var btnInitialize: AppCompatButton

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)
        setupMapView(rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initialize()
    }

    private fun initialize() {
        bottomSheetLayout = mapBinding.bottomSheet.bottomSheetRootView
        btnInitialize= mapBinding.btnInitialize
        currentLocationButton= mapBinding.currentLocationButton
        // BottomSheetLayout 에 View Binding 적용
        bottomSheetBinding = FragmentBottomSheetBinding.bind(bottomSheetLayout)
        // BottomSheetBehavior 에 layout 설정
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)

        // 작업 진행 중 View 다시 그릴 때 requestLayout() 사용
        bottomSheetLayout.layoutParams.height = getMaxHeightBasedOnScreenHeight()
        bottomSheetLayout.requestLayout()

        btnInitialize.setOnClickListener {
            toggleBottomSheetState()
        }

        bottomSheetLayout.setOnClickListener {
            toggleBottomSheetState()
        }

        // 일부분 layout 만 보여진 상태
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun getMaxHeightBasedOnScreenHeight(): Int {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels

        return (screenHeight * 0.50).toInt()
    }

    private fun toggleBottomSheetState() {
        bottomSheetBehavior.state =
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                BottomSheetBehavior.STATE_EXPANDED
            } else {
                BottomSheetBehavior.STATE_COLLAPSED
            }
    }

    private fun apiItemlist(district: String, page: Int) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(ProgressBar(requireContext()))
        dialog.show()
        Handler(Looper.getMainLooper()).postDelayed({
            ApiDefinition.GET_ITEM_LIST(district, page, size).setListener { response ->
                    isLoading = false
                    val newItems = response.itemDto
                    if (!newItems.isNullOrEmpty() && moredata) {
                        val startPosition = dataList.size
                        newItems.forEach { item ->
                            dataList.add(
                                MainItem(
                                    item.photo,
                                    item.court,
                                    item.caseNumber,
                                    item.location,
                                    item.minimumBidPrice,
                                    item.biddingPeriod,
                                    item.xcoordinate,
                                    item.ycoordinate
                                )
                            )
                            loadMarkersFromServer(district)

                        }
                        if (page == 0) {
                            // 첫 페이지라면 RecyclerView를 초기 설정
                            initRecycler(dataList, district)
                        } else {
                            bottomSheetBinding?.mainlistRecyclerView?.adapter?.notifyItemRangeInserted(
                                startPosition, newItems.size
                            )

                        }

                    } else {
                        Toast.makeText(requireContext(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show()
                        moredata = false
                    }
                    dialog.dismiss()

                }

                .setRequestHeaders(
                    mutableMapOf(
                        "Authorization" to "Bearer " + PreferenceUtil(requireContext()).init()
                            .start().getString(PREFERENCE_TOKEN)!!
                    )

                )

                .build(requireContext())
        }, 500) // 2000ms = 2초

    }

    private fun initialize(district: String, page: Int = 0) {
        Log.d("page", page.toString())
        Handler(Looper.getMainLooper()).postDelayed({
            apiItemlist(district, page)
        }, 2000)
    }


// 스크롤 이벤트 원할때만 뜨게 변경한 후 서버에서 페이징 적용

    private fun initRecycler(itemList: MutableList<MainItem>, district: String) {
        Log.d("initRecycler??????", itemList.toString())
        val recyclerAdapter = object : RecyclerView.Adapter<MainHolder>() {
            val myActualItemList = mutableListOf<MainItem>()

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
                recyclerBinding = ElementItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )

                return MainHolder(recyclerBinding, itemClickListener?.get())
            }

            override fun onBindViewHolder(
                holder: MainHolder, position: Int
            ) {
                holder.bind(itemList[position])
            }

            override fun getItemCount(): Int = itemList.size

            // 리스너 객체 참조를 recyclerView Adapter 에 전달
            // 약한 참조를 위해 WeakReference 설정
            fun setOnItemClickListener(listener: BaseFragment.OnItemClickListener) {
                itemClickListener = WeakReference(listener)
            }
        }
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        bottomSheetBinding?.mainlistRecyclerView?.apply {
            this.layoutManager = layoutManager
            this.itemAnimator = DefaultItemAnimator()
            adapter = recyclerAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!isLoading) {
                        val lastVisibleItemPosition =
                            (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                        val totalItemCount = recyclerView.adapter!!.itemCount
                        if (lastVisibleItemPosition == totalItemCount - 1 && moredata) { // 마지막 항목이 보일 때
                            loadMoreData(district)
                        }
                    }
                }
            })
        }

        // recyclerView 아이템 클릭 이벤트 설정
        recyclerAdapter.setOnItemClickListener(object : BaseFragment.OnItemClickListener {
            override fun onItemClick(position: Int) {

                // Bundle 을 이용해 position 에 해당하는 이미지 URL 넘기기
                requireActivity().supportFragmentManager.commit {
                    replace(
                        R.id.framelayout,
                        ItemLookUpFragment.indexImage(itemList[position].iconDrawable)
                    )
                }
            }
        })
    }

    private fun loadMoreData(district: String) {
        if (isLoading) return // 중복 호출 방지

        // 현재 스크롤 위치 저장
        val layoutManager =
            bottomSheetBinding?.mainlistRecyclerView?.layoutManager as LinearLayoutManager
        val currentScrollPosition = layoutManager.findFirstCompletelyVisibleItemPosition()

        isLoading = true
        currentPage++ // Increase the page number

        // 데이터 로드
        initialize(district, currentPage)

        // 데이터 로딩이 완료된 후 실행할 작업 설정 (데이터 로딩 완료 후에 호출해야 함)
        Handler(Looper.getMainLooper()).post {
            // 데이터 변경이 있더라도 스크롤 위치 유지
            layoutManager.scrollToPositionWithOffset(currentScrollPosition, 0)
            isLoading = false // 로딩 상태 업데이트
        }
    }


    override fun onMapReady(map: NaverMap) {
        MapUtil.printLog("MapFragment", "onMapReady called")
        naverMap = map
        naverMap.mapType = NaverMap.MapType.Basic

        MapUtil.moveCameraToPosition(naverMap, LatLng(36.6300312, 127.455085))
        MapUtil.configureMapUiSettings(naverMap, false)

        locationSource = FusedLocationSource(this@MapFragment, LOCATION_PERMISSION_REQUEST_CODE)
        MapUtil.setLocationSource(naverMap, locationSource)

        naverMap.addOnCameraChangeListener { _, _ ->
            MapUtil.printLog("MapFragment", "CameraChangeListener called")
            handleCameraChange()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            loadCurrentLocation()
        } else {
            // 위치 권한이 없는 경우 기본값으로 설정
            viewModel.districtLiveData.value = "상당구"
            loadMarkersFromServer("상당구")
        }
    }

    private fun setupMapView(rootView: View) {
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.mapView) as com.naver.maps.map.MapFragment?
            ?: com.naver.maps.map.MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.mapView, it).commit()
            }

        mapFragment.getMapAsync(this)
    }

    private fun handleCameraChange() {
        MapUtil.handleCameraChange(naverMap, lastKnownLatLng, onCameraMoved = {
            cameraMoved = true
            showReloadButton()
            cameraMoved = false
        }, updateLastKnownLatLng = { newLatLng ->
            lastKnownLatLng = newLatLng // 위치 업데이트
        }, shouldUpdateCameraMovement = { shouldMove ->
            cameraMoved = shouldMove // 카메라 움직임 업데이트
        })
    }


    //https://duzi077.tistory.com/263
    //https://manorgass.tistory.com/82
    private fun loadCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val currentLocationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                        CancellationTokenSource().token

                    override fun isCancellationRequested() = false
                })

            currentLocationTask.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    naverMap?.moveCamera(CameraUpdate.scrollTo(currentLatLng))
                }
            }.addOnFailureListener {
                Log.e("ERROR", "Failed to get location")
            }
        } else {
            Log.e("ERROR", "Location permission is not granted")
        }
    }

    // https://beeyoo0o0ncha.tistory.com/25
// 화면 가운데 좌표 -> 위치 알아내기
    private fun loadMarkersFromServer(district: String) {
        ApiDefinition.GET_ITEM_LIST(district, currentPage, size).setListener { response ->
                handleApiResults(response.itemDto)
            }.setRequestHeaders(
                mutableMapOf(
                    "Authorization" to "Bearer " + PreferenceUtil(requireContext()).init().start()
                        .getString(PREFERENCE_TOKEN)!!
                )
            ).build(requireContext())
    }

    fun sendAndRetrieveLocationInfo(district: String) {
        val requestData = DistrictRequest(district)
        initialize(district)

        ApiDefinition.GET_LOCATION_INFO.setRequestHeaders(
                mutableMapOf(
                    "Authorization" to "Bearer " + PreferenceUtil(requireContext()).init().start()
                        .getString(PREFERENCE_TOKEN)!!
                )
            ).setRequestParams(requestData).setListener {
                Log.d("sendAndRetrieveLocationInfo", "sendAndRetrieveLocationInfo")
                loadMarkersFromServer(district)

            }.build(requireContext())
    }

//    private fun addMarkerToMap(lat: Double, lng: Double, item: MainItem) {
//        MapUtil.addmarkers(lat, lng, naverMap, item, requireContext())
//    }

    private fun showReloadButton() {
        val reloadButton: Button = view?.findViewById(R.id.reloadButton) ?: return
        val geocoder = Geocoder(requireContext(), Locale.KOREAN)

        reloadButton.visibility = View.VISIBLE
        reloadButton.setOnClickListener {
            bottomSheetBinding?.bottomSheetRootView?.visibility = View.VISIBLE
            cameraMoved = false  // Reload 버튼을 클릭했을 때 cameraMoved 플래그를 초기화
            reloadButton.visibility = View.GONE
            moredata = true
            // 화면의 중심 좌표
            val center = naverMap?.cameraPosition?.target

            if (center != null) {
                MapUtil.getFromLocationAsync(geocoder,
                    center.latitude,
                    center.longitude,
                    object : GeocodeListener {
                        override fun onGeocodeReceived(list: List<Address>?) {
                            val district = list?.getOrNull(0)?.subLocality
                            if (district != null) {
                                viewModel.districtLiveData.value = district
                                Log.d("지금 구", district)
                                Toast.makeText(
                                    requireContext(),
                                    "조회하신 지역은 " + district + " 입니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // 서버로 데이터 전송
                                sendAndRetrieveLocationInfo(district)

                            } else {
                                val district = list?.getOrNull(0)?.thoroughfare
                                if (district != null) {
                                    Log.d("지금 지역", district)
                                    Toast.makeText(
                                        requireContext(),
                                        "조회하신 지역은 " + district + " 입니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    sendAndRetrieveLocationInfo(district)

                                }

                            }
                        }
                        override fun onError(e: Exception) {
                            Log.d("위도/경도", "입출력 오류")
                        }
                    })
            } else {
                Log.d("화면 중심 좌표", "네이버 맵 객체가 null입니다.")
            }
        }
    }

    interface GeocodeListener {
        fun onGeocodeReceived(addresses: List<Address>?)
        fun onError(e: Exception)
    }


    private fun handleApiResults(itemDtoList: List<ItemDto>) {
        MapUtil.handleApiResults(requireContext(), itemDtoList, naverMap)

    }


    // NaverGeocodingAPI
    interface NaverGeocodingAPI {
        @Headers("X-NCP-APIGW-API-KEY-ID: $Client_ID", "X-NCP-APIGW-API-KEY: $Client_Secret")
        @GET("map-reversegeocode/v2/gc")
        suspend fun reverseGeocode(
            @Query("coords") coords: String,
            @Query("orders") orders: String = "admcode",
            @Query("output") output: String = "json"
        ): GeocodingResponse
    }

    data class GeocodingResponse(val results: List<ResultItem>)
    data class ResultItem(val name: String, val code: Code)
    data class Code(val id: String, val type: String, val name: String)


    override fun onDestroy() {
        super.onDestroy()   // 화면이 종료될 때 모든 마커 제거


        markers.clear()     // 마커 리스트 비움
    }

}

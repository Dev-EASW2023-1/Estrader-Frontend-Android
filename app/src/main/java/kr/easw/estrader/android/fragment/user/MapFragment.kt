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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.easw.estrader.android.R
import kr.easw.estrader.android.databinding.ElementItemBinding
import kr.easw.estrader.android.databinding.FragmentBottomSheetBinding
import kr.easw.estrader.android.databinding.FragmentMainlistBinding
import kr.easw.estrader.android.definitions.ApiDefinition
import kr.easw.estrader.android.definitions.Client_ID
import kr.easw.estrader.android.definitions.Client_Secret
import kr.easw.estrader.android.definitions.PREFERENCE_TOKEN
import kr.easw.estrader.android.fragment.BaseFragment
import kr.easw.estrader.android.model.data.MainHolder
import kr.easw.estrader.android.model.dto.DistrictRequest
import kr.easw.estrader.android.model.dto.ItemDto
import kr.easw.estrader.android.model.dto.MainItem
import kr.easw.estrader.android.util.PreferenceUtil
import kr.easw.estrader.android.util.SharedViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.Locale


//https://xetown.com/questions/1532680
//지도당 속도
class MapFragment : BaseFragment<FragmentMainlistBinding>(FragmentMainlistBinding::inflate),
    OnMapReadyCallback {

    private lateinit var naverMap: NaverMap
    private var flag = false
    private lateinit var currentLocationButton: com.naver.maps.map.widget.LocationButtonView
    private lateinit var locationSource: FusedLocationSource
    private var lastKnownLatLng: LatLng? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val viewModel: SharedViewModel by activityViewModels()
    private var cameraMoved = false
    private var recyclerBinding: ElementItemBinding? = null
    private var itemClickListener: WeakReference<OnItemClickListener>? = null
    private var bottomSheetBinding: FragmentBottomSheetBinding? = null
    private val markers: MutableList<Marker> = mutableListOf()
    private var totalPageNumber: Int = 0
    private var moredata = true
    private var currentPage = 0
    private var size = 5
    private var isLoading = false
    private val dataList: MutableList<MainItem> = mutableListOf()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private val visibleThreshold = 1

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    private fun toggleBottomSheetState() {
        bottomSheetBehavior.state =
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                BottomSheetBehavior.STATE_EXPANDED
            } else {
                BottomSheetBehavior.STATE_COLLAPSED
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)
        setupMapView(rootView)
        val bottomSheetLayout: ConstraintLayout = rootView.findViewById(R.id.bottomSheetRootView)
        val btnInitialize: Button = rootView.findViewById(R.id.btn_initialize)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        bottomSheetBinding = FragmentBottomSheetBinding.bind(bottomSheetLayout)
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels
        val maxHeight = (screenHeight * 0.50).toInt() // 화면의 80%

        bottomSheetLayout.layoutParams.height = maxHeight
        bottomSheetLayout.requestLayout()

        btnInitialize.setOnClickListener {
            toggleBottomSheetState()
        }

        bottomSheetLayout.setOnClickListener {
            toggleBottomSheetState()
        }

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        currentLocationButton = rootView.findViewById(R.id.currentLocationButton)
        return rootView
    }

    private fun apiItemlist(district: String, page: Int) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(ProgressBar(requireContext()))
        dialog.show()
        Handler(Looper.getMainLooper()).postDelayed({
        ApiDefinition.GET_ITEM_LIST(district, page, size)
            .setListener { response ->
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
                    }
                    if (page == 0) {
                        // 첫 페이지라면 RecyclerView를 초기 설정
                        initRecycler(dataList, district)
                    } else {
                        bottomSheetBinding?.mainlistRecyclerView?.adapter?.notifyItemRangeInserted(
                            startPosition,
                            newItems.size
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
                    "Authorization" to "Bearer " + PreferenceUtil(requireContext()).init().start()
                        .getString(PREFERENCE_TOKEN)!!
                )

            )

            .build(requireContext())
        }, 2000) // 2000ms = 2초
    }


    private fun initialize(district: String, page: Int = 0) {
        Log.d("page", page.toString())
        Handler(Looper.getMainLooper()).postDelayed({
            apiItemlist(district, page)
        }, 2000) // 2000ms = 2초
    }


// 스크롤 이벤트 원할때만 뜨게 변경한 후 서버에서 페이징 적용

    private fun initRecycler(itemList: MutableList<MainItem>, district: String) {
        Log.d("initRecycler??????", itemList.toString())
        val recyclerAdapter = object : RecyclerView.Adapter<MainHolder>() {
            val myActualItemList = mutableListOf<MainItem>()

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
                    MainHolder {
                recyclerBinding = ElementItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )

                return MainHolder(recyclerBinding, itemClickListener?.get())
            }

            override fun onBindViewHolder(
                holder: MainHolder, position: Int
            ) {
                holder.bind(itemList[position])
//                val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.item_animation_fall_down)
//                holder.itemView.startAnimation(animation)
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
        Log.d("MapFragment", "onMapReady called")

        naverMap = map
        naverMap.mapType = NaverMap.MapType.Basic

        initializeMap()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
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

    private fun initializeMap() {
        naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(36.6300312, 127.455085)))
        naverMap.uiSettings.isLocationButtonEnabled = false
        currentLocationButton.map = naverMap

        locationSource = FusedLocationSource(this@MapFragment, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource

        naverMap.addOnCameraChangeListener { _, _ ->
            Log.d("MapFragment", "CameraChangeListener called")
            handleCameraChange()
        }
    }


    private fun handleCameraChange() {
        if (cameraMoved) {
            return
        }

        val currentLatLng = naverMap.cameraPosition.target
        if (lastKnownLatLng != null) {
            val distance = lastKnownLatLng!!.distanceTo(currentLatLng).toInt()
            if (distance >= 1000) { // 1000 미터 이상 움직였을 때
                Log.d("handleCameraChange", "호출한번만")
                cameraMoved = true
                showReloadButton()
                lastKnownLatLng = currentLatLng  // 현재의 위치를 마지막 알려진 위치로 업데이트
                cameraMoved = false  // 다시 false로 설정하여 다음 카메라 움직임도 체크
            }
        } else {
            lastKnownLatLng = currentLatLng
        }
    }


    fun getDistrictFromLocation(latLng: LatLng, callback: (String) -> Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://naveropenapi.apigw.ntruss.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(NaverGeocodingAPI::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.reverseGeocode("${latLng.longitude},${latLng.latitude}")
                val district = response.results[0].name
                withContext(Dispatchers.Main) {
                    callback(district)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    e.printStackTrace()
                    callback("상당구")  // 기본값으로 호출
                }
            }
        }
    }


    //https://duzi077.tistory.com/263
    //https://manorgass.tistory.com/82
    private fun loadCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val currentLocationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                        CancellationTokenSource().token

                    override fun isCancellationRequested() = false
                }
            )

            currentLocationTask.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    naverMap?.moveCamera(CameraUpdate.scrollTo(currentLatLng))
                    processLocationData(currentLatLng)
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
        Log.d("loadMarkersFromServer", "fsdffsdfdsf")
        ApiDefinition.GET_ITEM_LIST(district, currentPage, size)
            .setListener { response ->
                handleApiResults(response.itemDto)
            }
            .setRequestHeaders(
                mutableMapOf(
                    "Authorization" to "Bearer " + PreferenceUtil(requireContext()).init().start()
                        .getString(PREFERENCE_TOKEN)!!
                )
            )
            .build(requireContext())
    }

    fun sendAndRetrieveLocationInfo(district: String) {
        val requestData = DistrictRequest(district)
        initialize(district)

        ApiDefinition.GET_LOCATION_INFO
            .setRequestHeaders(
                mutableMapOf(
                    "Authorization" to "Bearer " + PreferenceUtil(requireContext()).init().start()
                        .getString(PREFERENCE_TOKEN)!!
                )
            )
            .setRequestParams(requestData)
            .setListener {
                Log.d("sendAndRetrieveLocationInfo", "sendAndRetrieveLocationInfo")
                loadMarkersFromServer(district)

            }
            .build(requireContext())
    }

    private fun addMarkerToMap(lat: Double, lng: Double, item: MainItem) {
        val marker = Marker().apply {
            position = LatLng(lat, lng)
            map = naverMap
        }
        markers.add(marker)
        val infoWindow = InfoWindow().apply {
            adapter = object : InfoWindow.DefaultTextAdapter(requireContext()) {
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

    private fun showReloadButton() {
        val reloadButton: Button = view?.findViewById(R.id.reloadButton) ?: return
        val geocoder = Geocoder(requireContext(), Locale.KOREAN)

        reloadButton.visibility = View.VISIBLE

        reloadButton.setOnClickListener {
            toggleBottomSheetState()

            cameraMoved = false  // Reload 버튼을 클릭했을 때 cameraMoved 플래그를 초기화
            reloadButton.visibility = View.GONE
            moredata = true
            // 화면의 중심 좌표
            val center = naverMap?.cameraPosition?.target

            if (center != null) {
                getFromLocationAsync(
                    geocoder,
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

    fun getFromLocationAsync(
        geocoder: Geocoder,
        latitude: Double,
        longitude: Double,
        listener: GeocodeListener
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


    private fun processLocationData(currentLatLng: LatLng) {
        val geocoder = Geocoder(requireContext(), Locale.KOREAN)
        getFromLocationAsync(
            geocoder,
            currentLatLng.latitude,
            currentLatLng.longitude,
            object : GeocodeListener {
                override fun onGeocodeReceived(list: List<Address>?) {
                    val district = list?.getOrNull(0)?.subLocality ?: "상당구"
                    viewModel.districtLiveData.value = district
                    loadMarkersFromServer(district)
                }

                override fun onError(e: Exception) {
                    Log.d("위도/경도", "입출력 오류")
                }
            })
    }


    fun getCurrentBounds(): LatLngBounds? {
        return naverMap.contentBounds
    }

    private fun handleApiResults(itemDtoList: List<ItemDto>) {
        for (marker in markers) {
            marker.map = null
        }
        markers.clear()

        val dataList: MutableList<MainItem> = mutableListOf()

        for (itemDto in itemDtoList) {
            dataList.add(
                MainItem(
                    itemDto.photo,
                    itemDto.court,
                    itemDto.caseNumber,
                    itemDto.location,
                    itemDto.minimumBidPrice,
                    itemDto.biddingPeriod,
                    itemDto.xcoordinate,
                    itemDto.ycoordinate
                )
            )

            val lat = itemDto.ycoordinate.toDoubleOrNull() ?: 0.0
            val lng = itemDto.xcoordinate.toDoubleOrNull() ?: 0.0

            addMarkerToMap(lat, lng, dataList.last())
        }
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

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.fragment_bottom_sheet)


        val confirmButton = bottomSheetDialog.findViewById<Button>(R.id.confirm_button)
        val rejectButton = bottomSheetDialog.findViewById<Button>(R.id.reject_button)

        confirmButton?.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        rejectButton?.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        bottomSheetDialog.show()
    }
}

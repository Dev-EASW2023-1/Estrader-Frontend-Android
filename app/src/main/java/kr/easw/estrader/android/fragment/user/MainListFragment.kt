package kr.easw.estrader.android.fragment.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import kr.easw.estrader.android.R
import kr.easw.estrader.android.databinding.ElementItemBinding
import kr.easw.estrader.android.databinding.FragmentMainlistBinding
import kr.easw.estrader.android.definitions.ApiDefinition
import kr.easw.estrader.android.definitions.PREFERENCE_TOKEN
import kr.easw.estrader.android.fragment.BaseFragment
import kr.easw.estrader.android.model.data.MainHolder
import kr.easw.estrader.android.model.dto.ItemPageRequestDTO
import kr.easw.estrader.android.model.dto.MainItem
import kr.easw.estrader.android.util.PreferenceUtil
import kr.easw.estrader.android.util.SharedViewModel
import java.lang.ref.WeakReference

/**
 * 사용자 전용 부동산 매각정보 리스트 Fragment
 * 리스트 항목을 누르면 ItemLookUpFragment 로 이동
 */
class MainListFragment : BaseFragment<FragmentMainlistBinding>(FragmentMainlistBinding::inflate){
    private val viewModel: SharedViewModel by activityViewModels()
    private var itemClickListener: WeakReference<OnItemClickListener>? = null
    private var recyclerBinding: ElementItemBinding? = null
    private lateinit var shimmerContainer: ShimmerFrameLayout
    private var page = 5
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        viewModel.districtLiveData.observe(viewLifecycleOwner, Observer { district ->
            initialize(district)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerBinding = null
    }

    // ViewHolder 에 사용할 DateList 초기화
    private fun initialize(district: String) {
        shimmerContainer = (binding as FragmentMainlistBinding).shimmerViewContainer
        shimmerContainer.startShimmer() // 스켈레톤 로딩 시작
        ApiDefinition.GET_ITEM_LIST.setRequestParams(
            ItemPageRequestDTO(district, page, page)
        ).setListener {
            val dataList: MutableList<MainItem> = mutableListOf()
            for(x in 0 until it.itemDto.size){
                dataList.add(MainItem(
                    it.itemDto[x].photo,
                    it.itemDto[x].court,
                    it.itemDto[x].caseNumber,
                    it.itemDto[x].location,
                    it.itemDto[x].minimumBidPrice,
                    it.itemDto[x].biddingPeriod,
                    it.itemDto[x].xcoordinate,
                    it.itemDto[x].ycoordinate,
                    it.itemDto[x].district
                ))
            }
            initRecycler(dataList)
            shimmerContainer.stopShimmer() // 스켈레톤 로딩 중지
            shimmerContainer.visibility = View.GONE // 스켈레톤 뷰 숨기기
        }
            .setRequestHeaders(mutableMapOf("Authorization" to "Bearer " + PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_TOKEN)!!))
            .build(requireContext())
        (binding as FragmentMainlistBinding).mainlistRecyclerView.visibility = View.VISIBLE // 실제 데이터 뷰 보이기
    }

    private fun initRecycler(itemList: MutableList<MainItem>) {
        val recyclerAdapter = object : RecyclerView.Adapter<MainHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup, viewType: Int
            ): MainHolder {
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
            fun setOnItemClickListener(listener: OnItemClickListener) {
                itemClickListener = WeakReference(listener)
            }
        }
        (binding as FragmentMainlistBinding).mainlistRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = recyclerAdapter
        }

        // recyclerView 아이템 클릭 이벤트 설정
        recyclerAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {

                // Bundle 을 이용해 position 에 해당하는 이미지 URL 넘기기
                requireActivity().supportFragmentManager.commit {
                    replace(R.id.framelayout,
                        ItemLookUpFragment.indexImage(itemList[position].iconDrawable)
                    )
                }
            }
        })
    }

}
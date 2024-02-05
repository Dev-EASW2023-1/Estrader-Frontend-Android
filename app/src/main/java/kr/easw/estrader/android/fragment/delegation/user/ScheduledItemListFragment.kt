package kr.easw.estrader.android.fragment.delegation.user

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.easw.estrader.android.abstracts.BasePagingAdapter
import kr.easw.estrader.android.databinding.ElementItemBinding
import kr.easw.estrader.android.databinding.FragmentScheduledItemListBinding
import kr.easw.estrader.android.definitions.ApiDefinition
import kr.easw.estrader.android.definitions.PREFERENCE_TOKEN
import kr.easw.estrader.android.fragment.BaseFragment
import kr.easw.estrader.android.model.dto.ItemPageRequestDTO
import kr.easw.estrader.android.model.dto.MainItem
import kr.easw.estrader.android.util.PreferenceUtil
import kr.easw.estrader.android.util.ViewUtil

class ScheduledItemListFragment : BaseFragment<FragmentScheduledItemListBinding>(FragmentScheduledItemListBinding::inflate) {
    private lateinit var pagingAdapter: BasePagingAdapter<MainItem, ElementItemBinding>
    private val dataList: MutableList<MainItem> = mutableListOf()
    private var currentPage: Int = 0
    private var size: Int = 30

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareListItem()
    }

    private fun initRecycler() {
        pagingAdapter = ViewUtil.setupScheduledItemListAdapter(this, dataList)
        binding.newRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            itemAnimator = DefaultItemAnimator()
            adapter = pagingAdapter
        }
    }

    private fun prepareListItem() {
        ApiDefinition.GET_ITEM_LIST.setRequestParams(
            ItemPageRequestDTO("상당구", currentPage, size)
        ).setListener { response ->
            response.itemDto.takeIf { it.isNotEmpty() }?.let {
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
                }
            }
            initRecycler()
        }.setRequestHeaders(
            mutableMapOf(
                "Authorization" to "Bearer " + PreferenceUtil(requireContext()).init()
                    .start().getString(PREFERENCE_TOKEN)!!
            )
        )
        .build(requireContext())
    }
}